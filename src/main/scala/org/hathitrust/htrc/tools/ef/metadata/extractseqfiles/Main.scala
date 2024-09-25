package org.hathitrust.htrc.tools.ef.metadata.extractseqfiles

import com.gilt.gfc.time.Timer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.hathitrust.htrc.tools.ef.metadata.extractseqfiles.Helper._
import org.hathitrust.htrc.tools.spark.utils.Helper.stopSparkAndExit

import java.io.File
import scala.language.reflectiveCalls

object Main {
  val appName: String = "extract-seq-files"

  def main(args: Array[String]): Unit = {
    val conf = new Conf(args.to(Seq))
    val inputPath = conf.inputPath().toString
    val outputPath = conf.outputPath().toString
    val outputFormat = conf.outputFormat()
    val numCores = conf.numCores.map(_.toString).getOrElse("*")

    // set up logging destination
    conf.sparkLog.foreach(System.setProperty("spark.logFile", _))
    System.setProperty("logLevel", conf.logLevel().toUpperCase)

    // set up Spark context
    val sparkConf = new SparkConf()
    sparkConf.setAppName(appName)
    sparkConf.setIfMissing("spark.master", s"local[$numCores]")
    val sparkMaster = sparkConf.get("spark.master")

    val spark = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()

    val sc = spark.sparkContext

    try {
      logger.info("Starting...")
      logger.info(s"Spark master: $sparkMaster")

      // record start time
      val t0 = System.nanoTime()

      conf.outputPath().mkdirs()

      val pathConverter: String => String = outputFormat match {
        case "stubbytree" => stubbytreePath
        case "pairtree" => pairtreePath
        case _ => throw new IllegalArgumentException(s"Invalid output format: $outputFormat")
      }

      sc.sequenceFile[String, String](inputPath)
        .foreach { case (htid, jsonEF) =>
          saveAsBz2Compressed(jsonEF, new File(outputPath, pathConverter(htid)))
        }

      // record elapsed time and report it
      val t1 = System.nanoTime()
      val elapsed = t1 - t0

      logger.info(f"All done in ${Timer.pretty(elapsed)}")
    }
    catch {
      case e: Throwable =>
        logger.error(s"Uncaught exception", e)
        stopSparkAndExit(sc, exitCode = 500)
    }

    stopSparkAndExit(sc)
  }

}
