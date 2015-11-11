package de.is24

import java.nio.file.{StandardOpenOption, Paths, Files}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._

object Starter extends App {
  implicit val actorSystem = ActorSystem("default")
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()
  val http = Http()

  val downloader = new MenuDownloader(http, new SoutLogger)

  val image = Await.result(downloader.download().map(PdfToImageConverter.convert), 25.seconds)
  Files.write(Paths.get("sodexo.png"), image, StandardOpenOption.CREATE)

  actorSystem.shutdown()
  actorSystem.awaitTermination()
}
