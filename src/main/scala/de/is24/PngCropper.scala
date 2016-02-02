package de.is24

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import org.apache.pdfbox.util.ImageIOUtil

import scala.collection.immutable.IndexedSeq

object PngCropper {
  val weekdays = Seq("monday", "tuesday", "wednesday", "thursday", "friday")
  val xOffset = 1226
  val yOffset = 1354
  val tileHeight = 1392
  val stripeHeight: Int = 5

  def extractWeekdays(img: BufferedImage)(implicit logger: SodexoLogger): Map[String, Array[Byte]] = {
    val orangeLines: IndexedSeq[Int] = (0 until img.getWidth - 1).flatMap {
      case x: Int =>
        val pixels = img.getRGB(x, img.getHeight / 2, 1, stripeHeight, null, 0, 1)
        val nextPixels = img.getRGB(x + 1, img.getHeight / 2, 1, stripeHeight, null, 0, 1)
        if (pixels.toSeq.forall(isOrange) && !nextPixels.toSeq.forall(isOrange)) Option(x)
        else None
    }

    logger.log("Extracting weekdays from image")
    val offsets = orangeLines.drop(1).zip(orangeLines.drop(2))
    weekdays.zip(offsets).map {
      case (day, (offset, offsetEnd)) =>
        val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
        ImageIOUtil.writeImage(img.getSubimage(offset, yOffset, offsetEnd - offset, tileHeight), "png", outputStream, ImageConstants.RESOLUTION, 1.0f)
        s"$day.png" -> outputStream.toByteArray
    }.toMap
  }

  def isOrange(rgb: Int): Boolean = {
    rgb == new Color(247, 150, 70).getRGB
  }
}
