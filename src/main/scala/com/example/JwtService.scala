package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import akka.http.scaladsl.server.directives.Credentials

object JwtService {
  private val jwtSecret = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
  private val algorithm = Algorithm.HMAC256(jwtSecret)

  def createToken(username: String): String = {
    JWT.create()
      .withSubject(username)
      .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 24 * 3600 * 1000))
      .sign(algorithm)
  }

  def validateJwt(credentials: Credentials): Option[String] = credentials match {
    case Credentials.Provided(token) =>
      try {
        val verifier = JWT.require(algorithm).build()
        val decoded = verifier.verify(token)
        Option(decoded.getSubject)
      } catch {
        case _: JWTVerificationException => None
      }
    case _ => None
  }
}