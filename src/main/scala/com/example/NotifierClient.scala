package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes.`application/json`
import spray.json._
import JsonFormats._
import scala.concurrent.{ExecutionContext, Future}

object NotifierClient {
  def sendFilesNotification(files: Seq[String])(implicit system: ActorSystem[_]): Future[HttpResponse] = {
    implicit val ec: ExecutionContext = system.executionContext
    val payload = FilesPayload(files).toJson.compactPrint

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9000/notify",
      entity = HttpEntity(`application/json`, payload)
    )

    Http().singleRequest(request).recover {
      case ex =>
        system.log.warn(s"Failed to send notification: ${ex.getMessage}")
        HttpResponse(StatusCodes.InternalServerError)
    }
  }
}