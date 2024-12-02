package com.pithy.auth.reddit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{
  headers,
  ContentTypes,
  FormData,
  HttpEntity,
  HttpMethods,
  HttpRequest,
  HttpResponse,
  StatusCodes
}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import java.util.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

import com.pithy.auth.reddit.JsonProtocols.tokenResponseFormat

/**
 * Handles Reddit authentication: exchange oneTimeCode for token
 */
object RetrieveToken {
  def authenticate(
    oneTimeCode: String,
    clientId: String,
    clientSecret: String
  ): Either[Throwable, TokenRetrievalHttpResponse] = {
    implicit val system: ActorSystem = ActorSystem("retrieve-token-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    // Encode credentials in the Authorization header using BasicHttpCredentials as instructed by Reddit
    val credentials = BasicHttpCredentials(clientId, clientSecret)
    val authorization = headers.Authorization(credentials)

    // request content in Json
    //    val requestContent: TokenRetrievalHttpPostRequest =
    //      TokenRetrievalHttpPostRequest(
    //        grant_type = "authorization_code",
    //        code = oneTimeCode,
    //        redirect_uri = RedditAuthRedirectUrl.getAuthenticationUrl
    //      )
    //    val requestContentJson: JsValue = requestContent.toJson

    // URL-encoded form data for the request body
    val requestContent = FormData(
      "grant_type" -> "authorization_code",
      "code" -> s"$oneTimeCode",
      "redirect_uri" -> s"${RedditAuthConfigLoader.getRedirectUri}"
    ).toEntity

    // Create http post request
    val retrieveTokenRequest: HttpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = "https://www.reddit.com/api/v1/access_token",
      headers = List(authorization, headers.`User-Agent`("Pithy-Prototype")),
      entity = requestContent
    )

    // make the request
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(retrieveTokenRequest)

    val responseFutureWithoutSideEffect
      : Future[Either[Throwable, TokenRetrievalHttpResponse]] = responseFuture
      .flatMap { response =>
        response.status.intValue() match {
          case statusCode if statusCode >= 200 && statusCode < 300 =>
            // Successfully received a response, now unmarshal it
            Unmarshal(response.entity)
              .to[TokenRetrievalHttpResponse]
              .map(Right(_))

          case statusCode if statusCode <= 400 && statusCode < 500 =>
            // Handles Client side errors
            Unmarshal(response.entity).to[String].map { errorBody =>
              if (errorBody.contains("unsupported_grant_type")) {
                Left(UnsupportedGrantType)
              } else if (errorBody.contains("NO_TEXT")) {
                Left(MissingCode)
              } else if (errorBody.contains("invalid_grant")) {
                Left(InvalidGrant)
              } else if (statusCode == 401) {
                Left(FourZeroOneResponse)
              } else {
                println(errorBody)
                Left(new Exception(s"Unknown error: $errorBody"))
              }
            }

          case statusCode if statusCode >= 500 =>
            // Handles Server side errors
            Unmarshal(response.entity).to[String].map { errorBody =>
              Left(
                new Exception(s"Please check Reddit Server status: $errorBody")
              )
            }

          case _ =>
            println(response)
            Future.successful(
              Left(new Exception(s"Unknown error: ${response.status}"))
            )
        }
      }
      .recover { case a =>
        println(a.getMessage)
        Left(NoResponse) // If no response at all
      }

    // wait 10 seconds for the response
    val result: Either[Throwable, TokenRetrievalHttpResponse] =
      try
        Await.result(responseFutureWithoutSideEffect, 15.seconds) match {
          case Right(code) => Right(code) // case when code is retrieved
          case Left(error) =>
            Left(error) // case when an exception occurs while retrieving code
        }
      catch {
        // case when user didn't click allow in time
        case _: TimeoutException =>
          Left(
            new TimeoutException(
              "Timed out while exchanging code with token from Reddit"
            )
          )
      }

    result
  }
}

/**
 * Post request body
 */
final case class TokenRetrievalHttpPostRequest(
  grant_type: String,
  code: String,
  redirect_uri: String
)

/**
 * Response from Reddit after token retrieval
 */
final case class TokenRetrievalHttpResponse(
  access_token: String,
  token_type: String,
  expires_in: Float,
  scope: String,
  refresh_token: String
) {
  def toMap: Map[String, String] = Map(
    "access_token" -> access_token,
    "token_type" -> token_type,
    "expires_in" -> expires_in.toString,
    "scope" -> scope,
    "refresh_token" -> refresh_token
  )
  override def toString: String =
    s"access_token: $access_token, token_type: $token_type, expires_in: $expires_in, scope: $scope, refresh_token: $refresh_token"
}

/**
 * Json formats for request body and response
 */
object JsonProtocols extends DefaultJsonProtocol {
  implicit val tokenRequestFormat
    : RootJsonFormat[TokenRetrievalHttpPostRequest] = jsonFormat3(
    TokenRetrievalHttpPostRequest
  )
  implicit val tokenResponseFormat: RootJsonFormat[TokenRetrievalHttpResponse] =
    jsonFormat5(TokenRetrievalHttpResponse)
}
