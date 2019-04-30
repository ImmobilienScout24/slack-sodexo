package de.is24

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import org.apache.pdfbox.util.ImageIOUtil

object PngCropper {
  val weekdays: Seq[String] = Seq("monday", "tuesday", "wednesday", "thursday", "friday")

  def extractWeekdays(img: BufferedImage)(implicit logger: SodexoLogger): Map[String, Array[Byte]] = {
    logger.log("Extracting weekdays from image")

    (1 until 6).map {
      colIndex =>
        val colWidth: Int = (img.getWidth - 54) / 6
        val xOffset: Int = colIndex * colWidth
        val xOffsetEnd: Int = xOffset + colWidth - 1
        val yOffset: Int = 404
        val yOffsetEnd: Int = 1894 - yOffset
        val day = weekdays(colIndex - 1)
        val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
        ImageIOUtil.writeImage(img.getSubimage(xOffset, yOffset, xOffsetEnd - xOffset, yOffsetEnd),
          "png", outputStream, ImageConstants.RESOLUTION, 1.0f)
        s"$day.png" -> outputStream.toByteArray
    }.toMap
  }
}
