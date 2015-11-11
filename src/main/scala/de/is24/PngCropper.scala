package de.is24

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import org.apache.pdfbox.util.ImageIOUtil

object PngCropper {
  val weekdays = Seq("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag")
  val xOffset = 1226
  val yOffset = 1354
  val tileWidth = 641
  val tileHeight = 1392

  def extractWeekdays(img: BufferedImage): Map[String, Array[Byte]] = {
    val offsets = (0 to 4).map { factor => xOffset + factor * tileWidth }
    weekdays.zip(offsets).map {
      case (day, offset) =>
        val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
        ImageIOUtil.writeImage(img.getSubimage(offset, yOffset, tileWidth, tileHeight), "png", outputStream, ImageConstants.RESOLUTION, 1.0f)
        s"$day.png" -> outputStream.toByteArray
    }.toMap
  }
}
