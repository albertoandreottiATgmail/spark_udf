/**
  * Created by Beto on 21/10/16.
  * Example usage of Dataframes API,udf and join
  */

package me.jpaa


import org.apache.spark.sql.functions._
import org.apache.spark.{SparkContext, SparkConf}

object UdfTest {

  def  main (args: Array[String]) {

    val sparkConf = new SparkConf().setMaster("spark://jose-Satellite-S55-B:7077").setAppName("my_udfs")
    sparkConf.setJars(Seq(
      "./lib/spark-csv_2.11-1.3.0.jar",
      "./lib/commons-csv-1.4.jar",
      "./lib/logback-core-1.1.3.jar",
      "./lib/logback-classic-1.1.3.jar"
    ))

    val sc =  new SparkContext(sparkConf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    val firstDf = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load("/home/jose/Downloads/fuzzy_join.csv")
    firstDf.registerTempTable("first")

    val secondDf = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true") // Use first line of all files as header
      .option("inferSchema", "true") // Automatically infer data types
      .load("fuzzy_join.csv")
    secondDf.registerTempTable("second")

    //udf to Join
    val compare  = (val1 :String, val2 :String) =>  true
    sqlContext.udf.register("compare", compare)

    val newDF = sqlContext.sql(s"""SELECT f_alias.c1, f_alias.c2, f_alias.c3 """ +
      s"""FROM `first` f_alias """ +
      s"""JOIN `second` s_alias ON compare(f_alias.c2, s_alias.c3)""")

    newDF.collect() foreach(print(_))

  }

}
