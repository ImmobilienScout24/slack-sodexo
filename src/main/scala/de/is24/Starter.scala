package de.is24

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

  Await.ready(downloader.download(), 25.seconds)
  actorSystem.shutdown()
  actorSystem.awaitTermination()
}
