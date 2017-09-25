# ApacheSpark_ML_Movie_Recommendation
Using Apache Spark and Alternating Least Squares method to provide Movie Recommendations 

The overarching goal of this project is to develop an efficient movie recommendation system using the Alternating Least Squares method and Apache Spark (to take advantage of the speed gains from distributed computing).

In a recommendation system such as used by Netflix (and many other consumer facing applications), the inputs include a list of users, a list of items (movies in this case), and the relationship betweem those (how have the users rated those movies historically). Based on this historical relationship, I want to predict how the users would rate movies they have not viewed.  Thus, we can use these predictions to provide recommendations.

I am using a free and available MovieLens dataset for historical data: https://grouplens.org/datasets/movielens/20m/

There are two main pieces to this project:

1. A scala file at /src/main/scala/ALSRecommendation.scala that makes use of the ALS algorithm derived from the MLlib

2. However, because MLlib is being phased out in favor of the DataFrame-based API, I wrote another snippet of recommendation code called "movie_recommendations.scala".

Running ALSRecommendation (Mac OS X):

1. Download Apache Spark and make sure you can run it from your terminal shell (use spark-submit command): https://spark.apache.org/

2. Make sure you can run Scala files by downloading sbt: http://www.scala-sbt.org/

3. Once sbt is downloaded, run "sbt compile" from terminal.

4. Run "sbt assembly" from terminal

5. Run "spark-submit --class org.vam.spark.ALSClient target/scala-2.11/apachespark_scala_2.11-1.0.jar ratings.csv movies.txt users.txt"

You can visit http://localhost:4040/executors/ to monitor the job as it is running.

Check how many cores are available: --master local[4] (cores available)

Running DataFrame-based ALS Code (movie_recommendations.scala) (Mac OS X):

1. Make you can access Spark Shell from terminal and that you have sbt up and running as above.

2. Run spark-shell 

3. Type :paste into shell

4. Paste the code in movie_recommendations.scala into the Spark Shell

5. Run and pursue the output 
