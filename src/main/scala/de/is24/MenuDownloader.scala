package de.is24

import java.time.ZonedDateTime
import java.time.temporal.IsoFields

import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{HttpExt, Http}
import akka.http.scaladsl.model.{HttpResponse, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

class MenuDownloader(http: HttpExt, week: Int)(implicit val actorMaterializer: ActorMaterializer, executionContext: ExecutionContext, logger: SodexoLogger) {


  def download(): Future[Array[Byte]] = {
    logger.log("Downloading menu")
    val weekWithLeadingZeroIfNecessary = f"$week%02d"

    val request: HttpRequest = HttpRequest(
      uri = s"https://www.sodexo-scoutlounge.de/assets/context/sodexo-scoutlounge/Speiseplan%20KW%20$weekWithLeadingZeroIfNecessary.pdf",
      method = HttpMethods.GET
    )

    http.singleRequest(request)
      .flatMap {
        handleErrorResponse(request)
      }
      .flatMap{ response =>
        logger.log("Downloaded menu successfully")
        Unmarshal(response.entity).to[Array[Byte]]
      }

  }

  private def handleErrorResponse(request: HttpRequest)(response: HttpResponse): Future[HttpResponse] = {
    if (response.status.isFailure()) createErrorResponse(request, response)
    else Future.successful(response)
  }

  private def createErrorResponse[T](request: HttpRequest, res: HttpResponse): Future[Nothing] = {
    val method = request.method.toString().toUpperCase
    Unmarshal(res.entity).to[String].flatMap { errorBody =>
      logger.log(s"$method '${request.uri}' failed with status ${res.status} and body '$errorBody'")
      Future.failed(new RuntimeException(s"$method ${request.uri}' failed with status ${res.status}"))
    }
  }
}
