package de.is24


import java.util
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.collection.JavaConverters._

import com.amazonaws.services.lambda.runtime.Context

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

class Sodexo {
  def handleCall(param: util.LinkedHashMap[String, Object], context: Context): java.util.Map[String, String] = {
    val responseUrl: Option[String] = param.asScala.get("response_url").map(_.asInstanceOf[String])
    val logger = context.getLogger
    logger.log(s"received : ${param.asScala}")

    responseUrl.foreach { url =>
      implicit val actorSystem = ActorSystem("default")
      implicit val ec = actorSystem.dispatcher
      implicit val materializer = ActorMaterializer()

      val JsonContentType = MediaTypes.`application/json`.withCharset(HttpCharsets.`UTF-8`)
      val http = Http()

      val entity = HttpEntity(JsonContentType,
        s"""{
    "attachments": [
    {
      "fallback": "No food today",
      "image_url": "https://www.google.de/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"
    }
    ],
    "response_type": "in_channel",
    "text": "Aktuelles Menue"
  }""")

      val request: HttpRequest = HttpRequest(
        uri = url,
        entity = entity,
        method = HttpMethods.POST
      )

      val httpResponse: Future[HttpResponse] = http.singleRequest(request)
        .map { response: HttpResponse =>
          if (response.status.isFailure()) {
            logger.log(s"HTTP response failed: ${response.status.intValue}")
          }
          response
        }
      Await.ready(httpResponse, 25.seconds)
      actorSystem.shutdown()
      actorSystem.awaitTermination()
    }

    Map[String, String]().asJava
  }
}
