package de.is24

import java.io.ByteArrayInputStream

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.event.{ProgressEventType, ProgressEvent, ProgressListener}
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManager

import scala.concurrent.{Promise, ExecutionContext, Future}

class ImageUploader(bucketName: String)(implicit logger: SodexoLogger) {

  def uploadImages(imageNamesToRaw: Map[String, Array[Byte]])(implicit ec: ExecutionContext): Future[Unit] = {
    logger.log(s"Uploading ${imageNamesToRaw.keys.toSeq}")
    val transferManager = new TransferManager(new DefaultAWSCredentialsProviderChain().getCredentials)

    val uploadResult = Future.sequence(imageNamesToRaw.map {

      case (imageName: String, rawImage: Array[Byte]) =>

        val metadata = new ObjectMetadata()
        metadata.setContentLength(rawImage.length)
        metadata.setContentType("image/png")
        val upload = transferManager.upload(bucketName, imageName, new ByteArrayInputStream(rawImage), metadata)

        val promise = Promise[Unit]()

        upload.addProgressListener(new ProgressListener {
          override def progressChanged(progressEvent: ProgressEvent): Unit = {
            progressEvent.getEventType match {
              case ProgressEventType.TRANSFER_FAILED_EVENT =>
                logger.log(s"Transfer of $imageName failed")
                promise.tryFailure(new Exception(s"Transfer of $imageName failed"))
              case ProgressEventType.TRANSFER_CANCELED_EVENT =>
                logger.log(s"Transfer of $imageName canceled by someone™")
                promise.tryFailure(new Exception(s"Transfer of $imageName canceled"))
              case ProgressEventType.TRANSFER_COMPLETED_EVENT =>
                logger.log(s"Transfer of $imageName successful")
                promise.success(())
              case _ => ()
            }
          }
        })

        promise.future

    }).map(_ => ())
    uploadResult.andThen {
      case _ =>
        logger.log("Shutting down..")
        transferManager.shutdownNow()
    }
  }
}
