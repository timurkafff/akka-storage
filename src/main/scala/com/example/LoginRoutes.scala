package com.example

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{StatusCodes, ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods
import JsonFormats._

class LoginRoutes(hardcodedUser: String, hardcodedPassword: String) {
  val route: Route =
    path("login") {
      options {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, ""))
      } ~
      post {
        formFieldMap { fields =>
          val username = fields.getOrElse("username", "")
          val password = fields.getOrElse("password", "")
          if (username == hardcodedUser && password == hardcodedPassword) {
            val token = JwtService.createToken(username)
            complete(Token(token))
          } else {
            complete(StatusCodes.Unauthorized -> "Invalid credentials")
          }
        }
      }
    }
} 