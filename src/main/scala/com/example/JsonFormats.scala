package com.example

import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

case class FilesPayload(files: Seq[String])

object JsonFormats {
  implicit val tokenFormat: RootJsonFormat[Token] = jsonFormat1(Token.apply)
  implicit val uploadedFormat: RootJsonFormat[Uploaded] = jsonFormat1(Uploaded.apply)
  implicit val filesPayloadFormat: RootJsonFormat[FilesPayload] = jsonFormat1(FilesPayload.apply)
}