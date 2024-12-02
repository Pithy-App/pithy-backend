package com.pithy.reddit

import akka.actor.ActorSystem

import cats.data.EitherT
import com.pithy.errors.{AppError, RequestError}
import com.pithy.reddit.requests.{GetCommentTreeFromPost, GetCommentTreeFromPostParams}
import com.pithy.reddit.requests.RedditUtils.extractCommentId36FromLink
import com.pithy.reddit.responses.DecodedGetCommentTreeFromPostBuilder
import com.pithy.reddit.responses.ResponseDecoder.decodeResponseGetCommentTreeFromPost
import com.pithy.json.JsonHandler.{parseJson, saveTempCopyAsJson}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.control.NonFatal

object RedditMain {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  def main(
    input: RedditMainInput,
    prod: Boolean = true
  ): RedditMainOutput = {
    implicit lazy val system: ActorSystem = ActorSystem(
      "reddit-api-request-system"
    )
    implicit lazy val executionContext: ExecutionContextExecutor =
      system.dispatcher


    val accessToken: String = ""
    val redditRequestParams: GetCommentTreeFromPostParams =
      extractCommentId36FromLink(input.postUrl)
        .fold(
          throwable => throw throwable,
          id36 => GetCommentTreeFromPostParams(article = id36)
        )

    val platformResponse: Future[Either[RequestError, String]] =
      new GetCommentTreeFromPost(accessToken, redditRequestParams).send()

    // only saves a copy if run locally
    if (!prod) {
      for {
        response <- EitherT(platformResponse)
        json <- EitherT(Future.successful(parseJson(response)))
        _ <- EitherT(
          Future.successful(
            saveTempCopyAsJson(
              json,
              "platform/src/main/resources/redditResponse.json"
            )
          )
        )
      } yield ()
    }

    val decodedResponse
      : EitherT[Future, AppError, DecodedGetCommentTreeFromPostBuilder] = for {
      response <- EitherT(platformResponse)
      decodedResponse <- EitherT(
        Future.successful(decodeResponseGetCommentTreeFromPost(response))
      )
    } yield decodedResponse

    // TODO: PITH-38
    Await.result(
      decodedResponse.value
        .map {
          case Right(decodedResponse) =>
            new RedditMainOutput(decodedResponse)
          case Left(failure) =>
            logger.error("An error occurred: " + failure.toString)
            throw new Exception(failure.toString)
        }
        .recover { case NonFatal(e) =>
          logger.error("An unexpected error occurred: " + e.toString)
          throw new Exception(e.toString)
        },
      3.seconds // Specify an appropriate timeout
    )
  }
}
