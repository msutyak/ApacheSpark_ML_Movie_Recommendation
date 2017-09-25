package org.vam.spark

import scala.collection.mutable
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD

object ALSClient {

  def main(args: Array[String]) {
    
    /* environment set up */
    val conf = new SparkConf().setAppName("ALS Recommendation System")

    /* Initializing Spark Kryo serialization */
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array(classOf[mutable.BitSet], classOf[Rating], classOf[MatrixFactorizationModel]))
      .set("spark.kryoserializer.buffer", "64m")

    /* Setting checkpointing to preclude serialization over large number of iterations */
    val sc = new SparkContext(conf)
    sc.setCheckpointDir("MovieRecommendationCheckPoint")
    
    /* Load and parse the movies rating file and movies mapping file included in repo */
    /* Movies rating schema: userid,movieid,rating,timestamp */
    /* Movies mapping file schema: movieid,movie name,genre */
    val ratings_file = sc.textFile(args(0))
    val movie_mapping_file = sc.textFile(args(1))

    /* Load list of users that you want recommendations for (top 25 movies) */
    val users_file = sc.textFile(args(2))

    val users = users_file.map { line => line.stripLineEnd.toInt
    }.collect()

    /* Number of hidden features */
    val features = 10

    /* Regularization parameter */
    val lambda = 0.02
    
    /* Number of steps before convergence */
    val numIterations = 1000

    /* Load ratings file and build ratings input RDD of (user, product, rating) triples */
    val ratings = ratings_file.map { line =>
      val fields = line.split(",")
      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }.cache()

    /* Ratings grouped by user id. userid,[rating1,...] */
    val ratingsGroupedByUser = ratings.map(rating => (rating.user, rating)).groupByKey().persist()

    /* UserID-MovieID pair */
    val userProductsPair = ratings.map(rating => (rating.user, rating.product))

    /* Load movie mapping file and build a Map of (MovieID,MovieName) pair */
    val movies = movie_mapping_file.map { line =>
      val fields = line.split(",")
      (fields(0).toInt, fields(1))
    }.collect().toMap

    /* Train the model */
    val model = {
      new ALS().setRank(features).setLambda(lambda).setIterations(numIterations).setImplicitPrefs(false).run(ratings)
    }

    /* Recommended Movie Name and UserID for each movie */
    val movies_name = users.map { user =>

      /* Fetch all the rated products for each user */
      val rated_products: List[Int] = ratingsGroupedByUser.lookup(user)(0).map(rat => rat.product).toList

      /* Remove movies which have not been rated by the user */
      val unrated_products = sc.parallelize(movies.keys.filter(!rated_products.contains(_)).toSeq)

      /* Fetch top 25 predicted movies based on the rating for all the unrated movies */
      val recommendation = model.predict(unrated_products.map(product => (user, product))).sortBy(-_.rating).take(25)

      /* Recommend the movie name along with the UserID. */
      val movies_name = sc.parallelize(recommendation.map(r => (user, movies(r.product))))

      movies_name
    }
    
    /* Save recommended movies as a file in HDFS */
    var i = 0
    val final_value= movies_name.foreach{rdd=>
    rdd.saveAsTextFile("ALSMovieRecommend/"+i)
      i = i+1
    }
    sc.stop()
  }
}
