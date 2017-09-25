name := "ApacheSpark_Scala"

version := "1.0"

scalaVersion := "2.11.8"

val sparkVersion = "2.2.0"


libraryDependencies += "org.apache.spark" %%"spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"