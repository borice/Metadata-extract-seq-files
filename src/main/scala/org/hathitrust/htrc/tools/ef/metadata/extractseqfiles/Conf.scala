package org.hathitrust.htrc.tools.ef.metadata.extractseqfiles

import java.io.File

import org.rogach.scallop.{Scallop, ScallopConf, ScallopHelpFormatter, ScallopOption, SimpleOption}

class Conf(args: Seq[String]) extends ScallopConf(args) {
  appendDefaultToDescription = true
  helpFormatter = new ScallopHelpFormatter {
    override def getOptionsHelp(s: Scallop): String = {
      super.getOptionsHelp(s.copy(opts = s.opts.map {
        case opt: SimpleOption if !opt.required =>
          opt.copy(descr = "(Optional) " + opt.descr)
        case other => other
      }))
    }
  }

  private val (appTitle, appVersion, appVendor) = {
    val p = getClass.getPackage
    val nameOpt = Option(p).flatMap(p => Option(p.getImplementationTitle))
    val versionOpt = Option(p).flatMap(p => Option(p.getImplementationVersion))
    val vendorOpt = Option(p).flatMap(p => Option(p.getImplementationVendor))
    (nameOpt, versionOpt, vendorOpt)
  }

  version(appTitle.flatMap(
    name => appVersion.flatMap(
      version => appVendor.map(
        vendor => s"$name $version\n$vendor"))).getOrElse(Main.appName))


  val sparkLog: ScallopOption[String] = opt[String]("spark-log",
    descr = "Where to write logging output from Spark to",
    argName = "FILE",
    noshort = true
  )

  val logLevel: ScallopOption[String] = opt[String]("log-level",
    descr = "The application log level; one of INFO, DEBUG, OFF",
    argName = "LEVEL",
    default = Some("INFO"),
    validate = level => Set("INFO", "DEBUG", "OFF").contains(level.toUpperCase)
  )

  val numCores: ScallopOption[Int] = opt[Int]("cores",
    descr = "The number of CPU cores to use (if not specified, uses all available cores)",
    required = false,
    argName = "N",
    validate = (n: Int) => n > 0 && n <= Runtime.getRuntime.availableProcessors()
  )

//  val numPartitions: ScallopOption[Int] = opt[Int]("num-partitions",
//    descr = "The number of partitions to split the input set of HT IDs into, " +
//      "for increased parallelism",
//    required = false,
//    argName = "N",
//    validate = 0 <
//  )

  val outputPath: ScallopOption[File] = opt[File]("output",
    descr = "Write the output to DIR",
    required = true,
    argName = "DIR"
  )

  val outputFormat: ScallopOption[String] = opt[String]("format",
    descr = "One of pairtree or stubbytree",
    argName = "pairtree|stubbytree",
    default = Some("pairtree"),
    validate = Set("pairtree", "stubbytree").contains
  )

  val inputPath: ScallopOption[File] = trailArg[File]("input",
    descr = "The path to the folder containing the sequence file parts"
  )

  validateFileIsDirectory(inputPath)
  verify()
}
