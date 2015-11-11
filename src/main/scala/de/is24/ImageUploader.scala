package de.is24

import java.io.ByteArrayInputStream

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManager

import scala.concurrent.{ExecutionContext, Future}

class ImageUploader(bucketName: String) {

  private def uploadImages(imageNamesToRaw: Map[String, Array[Byte]])(implicit ec: ExecutionContext): Future[Unit] = {
    val transferManager = new TransferManager(new DefaultAWSCredentialsProviderChain().getCredentials)

    val uploadResult = Future.sequence(imageNamesToRaw.map {

      case (imageName: String, rawImage: Array[Byte]) =>

        val metadata = new ObjectMetadata()
        metadata.setContentLength(rawImage.length)
        metadata.setContentType("image/png")
        val upload = transferManager.upload(bucketName, imageName, new ByteArrayInputStream(rawImage), metadata)

        Future {
          upload.waitForUploadResult()
        }
    }).map(_ => ())
    uploadResult.onComplete {
      case _ => transferManager.shutdownNow()
    }
    uploadResult
  }
}
