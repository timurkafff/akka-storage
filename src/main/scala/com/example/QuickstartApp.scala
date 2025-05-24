package com.example
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods
import scala.util.Failure
import scala.util.Success

object QuickstartApp {
  // Расширенные CORS headers
  val corsHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With"),
    `Access-Control-Allow-Methods`(HttpMethods.GET, HttpMethods.POST, HttpMethods.OPTIONS, HttpMethods.PUT, HttpMethods.DELETE),
    `Access-Control-Allow-Credentials`(true)
  )

  // CORS middleware для всех маршрутов
  def addCorsHeaders(route: Route): Route = {
    respondWithHeaders(corsHeaders) {
      route
    }
  }

  // OPTIONS handler для preflight requests
  val optionsRoute: Route = {
    options {
      complete("OK")
    }
  }

  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext
    val futureBinding = Http().newServerAt("0.0.0.0", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val storageRoutes = new StorageRoutes()(context.system).routes
      val loginRoutes = new LoginRoutes("admin", "password").route
      
      val allRoutes = addCorsHeaders {
        optionsRoute ~ storageRoutes ~ loginRoutes
      }
      
      startHttpServer(allRoutes)(context.system)
      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }
}