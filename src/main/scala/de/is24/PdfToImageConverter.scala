package de.is24

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.util.ImageIOUtil

object PdfToImageConverter {

  private val resolution = 300

  def convert(pdfBytes: Array[Byte]): Array[Byte] = {

    val document = PDDocument.load(new ByteArrayInputStream(pdfBytes))
    val pages = document.getDocumentCatalog.getAllPages.asInstanceOf[java.util.List[PDPage]]
    val firstPage = pages.get(0)
    val image = firstPage.convertToImage(BufferedImage.TYPE_INT_RGB, resolution)
    val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    ImageIOUtil.writeImage(image, "png", outputStream, resolution, 1.0f)
    outputStream.toByteArray
  }

}
