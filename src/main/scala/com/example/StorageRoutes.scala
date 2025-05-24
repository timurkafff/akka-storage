package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{StatusCodes, HttpEntity, ContentTypes}
import akka.http.scaladsl.server.Route
import akka.actor.typed.ActorSystem
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.Future
import akka.http.scaladsl.model.Multipart
import akka.http.scaladsl.server.directives.Credentials
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import JsonFormats._
import spray.json._
import scala.concurrent.ExecutionContext

class StorageRoutes(implicit val system: ActorSystem[_]) {
  private val storageDir = "storage"
  private val hardcodedUser = "admin"
  private val hardcodedPassword = "password"
  implicit val ec: ExecutionContext = system.executionContext

  Files.createDirectories(Paths.get(storageDir))

  val protectedRoutes: Route =
    authenticateOAuth2(realm = "secure-site", authenticator = JwtService.validateJwt _) { userName =>
      concat(
        path("upload") {
          post {
            entity(as[Multipart.FormData]) { formData =>
              val uploaded = formData.parts.mapAsync(1) {
                case b if b.filename.isDefined =>
                  val fileName = b.filename.get
                  val filePath = Paths.get(storageDir, fileName)
                  val sink = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                  b.entity.dataBytes.runForeach(chunk => sink.write(chunk.toArray))
                    .andThen { case _ => sink.close() }(system.executionContext)
                    .map(_ => fileName)(system.executionContext)
                case _ => Future.successful("")
              }.runFold(Seq.empty[String])(_ :+ _)

              onSuccess(uploaded) { files =>
                val uploadedFiles = files.filter(_.nonEmpty)
                if (uploadedFiles.nonEmpty) {
                  NotifierClient.sendFilesNotification(uploadedFiles)
                }
                complete(Uploaded(uploadedFiles))
              }
            }
          }
        },
        path("download" / Segment) { fileName =>
          get {
            val filePath = Paths.get(storageDir, fileName)
            if (Files.exists(filePath)) {
              val fileBytes = Files.readAllBytes(filePath)
              complete(HttpEntity(ContentTypes.`application/octet-stream`, fileBytes))
            } else {
              complete(StatusCodes.NotFound -> "File not found")
            }
          }
        }
      )
    }

  val routes: Route = protectedRoutes
}