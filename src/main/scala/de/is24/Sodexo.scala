package de.is24


import java.time.ZonedDateTime
import java.time.temporal.IsoFields
import java.util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.amazonaws.services.lambda.runtime.Context

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._

class Sodexo {
  def handleCall(param: util.LinkedHashMap[String, Object], context: Context): java.util.Map[String, String] = {
    val bucketName = "sodexo-slack"
    implicit val logger: SodexoLogger = new LoggerWrapper(context.getLogger)
    logger.log(s"received : ${param.asScala}")
    val nextWeek: Int = ZonedDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) + 1

    implicit val actorSystem = ActorSystem("default")
    implicit val ec = actorSystem.dispatcher
    implicit val materializer = ActorMaterializer()
    val http = Http()

    val downloader = new MenuDownloader(http, nextWeek)
    try {
      Await.result(
        downloader.download()
          .map(PdfToImageConverter.convert)
          .map(PngCropper.extractWeekdays)
          .flatMap(new ImageUploader(bucketName, nextWeek).uploadImages),
        3 minutes)
    } finally {
      logger.log("Shutting down system")
      actorSystem.shutdown()
      actorSystem.awaitTermination()
    }

    Map[String, String]().asJava
  }
}
