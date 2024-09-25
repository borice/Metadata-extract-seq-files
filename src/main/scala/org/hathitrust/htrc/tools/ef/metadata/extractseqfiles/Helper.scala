package org.hathitrust.htrc.tools.ef.metadata.extractseqfiles

import java.io._
import java.nio.file.Paths
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.hathitrust.htrc.data.HtrcVolumeId
import org.slf4j.{Logger, LoggerFactory}

import scala.io.Codec
import scala.util.Using

object Helper {
  @transient lazy val logger: Logger = LoggerFactory.getLogger(Main.appName)

  implicit class StringEx(s: String) {
    def takeEvery(n: Int): String = {
      require(n >= 0)
      (0 until s.length by n).map(s.charAt).mkString
    }
  }

  def saveAsBz2Compressed(s: String, toFile: File, codec: Codec = Codec.UTF8): Unit = {
    if (toFile.exists()) return;

    Option(toFile.getParentFile).foreach { parent =>
      if (!parent.mkdirs && !parent.isDirectory)
        throw new IOException(s"Directory $parent could not be created")
    }
    val compressedStream = new BZip2CompressorOutputStream(new BufferedOutputStream(new FileOutputStream(toFile)))
    Using.resource(new OutputStreamWriter(compressedStream, codec.charSet))(_.write(s))
  }

  def pairtreePath(htid: String): String = {
    HtrcVolumeId
      .parseUnclean(htid)
      .map(_.toPairtreeDoc)
      .get
      .extractedFeaturesPath
  }

  def stubbytreePath(htid: String): String = {
    HtrcVolumeId
      .parseUnclean(htid)
      .map { id =>
        val (libId, volId) = id.partsClean
        val stubbyPart = volId.takeEvery(3)
        Paths.get(libId, stubbyPart, s"$libId.$volId.json.bz2").toString
      }
      .get
  }
}