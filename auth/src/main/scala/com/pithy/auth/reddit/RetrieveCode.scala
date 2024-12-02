package com.pithy.auth.reddit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import java.util.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future, Promise}
import scala.util.{Either, Success}

/**
 * Retrieve token after user has authenticated or return None if user denies access
 */
object RetrieveCode {
  def retrieveCode(): Either[Throwable, String] = {
    implicit val system: ActorSystem = ActorSystem("oauth2-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    // Promise to control when the server shuts down. Evaluates to code if successful
    val shutdownPromise = Promise[Either[Throwable, String]]()

    // set up route to listen to
    val route: Route = path("oauth2" / "callback") {
      get {
        parameters(Symbol("code"), Symbol("state")) { (code, state) =>
          if (state == RedditAuthConfigLoader.getState) {
            complete {
              println(s"\n Received code: $code, state: $state \n")
              shutdownPromise.success(Right(code))
              StatusCodes.OK
            }
          } else {
            println("Received someone else's state. Keep waiting.")
            reject
          }
        }
      } ~
        parameter("error") { error =>
          complete {
            val retrieveCodeException = error match {
              case "access_denied" =>
                AccessDenied
              case "unsupported_response_type" =>
                UnsupportedResponseType
              case "invalid_scope" =>
                InvalidScope
              case "invalid_request" =>
                InvalidRequest
            }
            shutdownPromise.success(Left(retrieveCodeException))
            StatusCodes.BadRequest
          }
        }
    }

    // TODO PITH-9: remove local port binding when deploying to a cloud service
    // local server to listen to the public domain through ngrok
    val bindingFuture: Future[Http.ServerBinding] =
      Http().newServerAt("localhost", 8080).bind(route)

    println(
      s"Server started at http://localhost:8080. Waiting for OAuth2 callback..."
    )

    // no failure handling here because the server will always shut down unless fatal error which will be raised automatically
    shutdownPromise.future.onComplete {
      Success(_) -> bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }

    // wait 1 minute before application expires
    val result: Either[Throwable, String] =
      try
        Await.result(shutdownPromise.future, 1.minute) match {
          case Right(code) => Right(code) // case when code is retrieved
          case Left(error) =>
            Left(error) // case when an exception occurs while retrieving code
        }
      catch {
        // case when user didn't click allow in time
        case _: TimeoutException =>
          Left(
            new TimeoutException(
              "Link expired. User didn't allow access in time"
            )
          )
      }

    result
  }
}
