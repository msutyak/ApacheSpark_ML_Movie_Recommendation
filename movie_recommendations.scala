val df = spark.read.option("header", "true").option("inferSchema", "true").csv("ratings.csv")
df.show

import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.ml.evaluation.RegressionEvaluator


val als = new ALS()
  .setMaxIter(5)
  .setRegParam(0.01)
  .setUserCol("UserID")
  .setItemCol("Movie")
  .setRatingCol("Rating")
  
val alsModel = als.fit(df)
alsModel.transform(df).show

alsModel.recommendForAllItems(3).show
alsModel.recommendForAllUsers(3).show

alsModel.recommendForAllItems(3).printSchema



//evaluate how well the model is trained on the test data
val predictions = alsModel.transform(df)

val evaluator = new RegressionEvaluator()
  .setMetricName("rmse")
  .setLabelCol("rating")
  .setPredictionCol("prediction")
val rmse = evaluator.evaluate(predictions)