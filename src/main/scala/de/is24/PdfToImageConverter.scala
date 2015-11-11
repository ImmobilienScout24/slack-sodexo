package de.is24

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.util.ImageIOUtil

object PdfToImageConverter {

  def convert(pdfBytes: Array[Byte]): BufferedImage = {

    val document = PDDocument.load(new ByteArrayInputStream(pdfBytes))
    val pages = document.getDocumentCatalog.getAllPages.asInstanceOf[java.util.List[PDPage]]
    val firstPage = pages.get(0)
    firstPage.convertToImage(BufferedImage.TYPE_INT_RGB, ImageConstants.RESOLUTION)
  }

}
