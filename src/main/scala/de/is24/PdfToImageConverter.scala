package de.is24

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import resource._

object PdfToImageConverter {

  def convert(pdfBytes: Array[Byte])(implicit logger: SodexoLogger): BufferedImage = {

    logger.log("converting PDF to image")
    managed(PDDocument.load(new ByteArrayInputStream(pdfBytes))).acquireAndGet { document =>
      val pages = document.getDocumentCatalog.getAllPages.asInstanceOf[java.util.List[PDPage]]
      val firstPage = pages.get(0)
      val image = firstPage.convertToImage(BufferedImage.TYPE_INT_RGB, ImageConstants.RESOLUTION)
      logger.log("conversion done")
      image
    }
  }

}
