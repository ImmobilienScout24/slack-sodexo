package de.is24

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, StandardOpenOption, Paths}
import java.time.ZonedDateTime
import java.time.temporal.IsoFields

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.apache.pdfbox.util.ImageIOUtil

import scala.concurrent.Await
import scala.concurrent.duration._

object Starter extends App {
  implicit val actorSystem = ActorSystem("default")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()
  val http = Http()
  implicit val logger = new StdoutLogger
  val week: Int = ZonedDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)


  val downloader = new MenuDownloader(http, week)

  val image = Await.result(downloader.download().map(PdfToImageConverter.convert), 25.seconds)
  val outputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
  ImageIOUtil.writeImage(image, "png", outputStream, ImageConstants.RESOLUTION, 1.0f)
  Files.write(Paths.get("sodexo.png"), outputStream.toByteArray, StandardOpenOption.CREATE)

  val weekdayImages = PngCropper.extractWeekdays(image)

    weekdayImages.foreach {
    case (imagename, bytes) =>
      Files.write(Paths.get(imagename), bytes, StandardOpenOption.CREATE)
  }

  val imgUploader = new ImageUploader("sodexo-slack", week)
  Await.result(imgUploader.uploadImages(weekdayImages), 2 minutes)

  actorSystem.shutdown()
  actorSystem.awaitTermination()
}
