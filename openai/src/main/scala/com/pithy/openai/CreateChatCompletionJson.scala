package com.pithy.openai

import com.pithy.openai.api.{DecodedResponseSchema, RequestMessageSchema}
import io.cequence.openaiscala.domain.settings.CreateChatCompletionSettings
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json
import io.circe.generic.auto._
import io.circe.parser._

import java.io.PrintWriter
import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Success, Try}

/**
 * Start 1 conversation with the chat model and get a json response
 * https://github.com/cequence-io/openai-scala-client/blob/master/openai-core/src/main/scala/io/cequence/openaiscala/service/OpenAIServiceConsts.scala#L9
 */
object CreateChatCompletionJson extends Service {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  override protected def run(
    message: RequestMessageSchema,
    settings: CreateChatCompletionSettings
  ): Future[Either[Throwable, DecodedResponseSchema]] = {

    logger.info("Start creating chat completion...")

    Try {
      service
        .createChatCompletion(
          messages = message.content,
          settings = settings
        )
        .transform {
          case Success(response) =>
            logger.info("Received chat completion response.")

            val responseContent = messageContent(response)

            val writer =
              new PrintWriter(
                "/tmp/example_response.txt"
              )
            writer.write(responseContent)
            writer.close()

            // convert the response to OpenAiResponseSchema case class
            logger.info(
              "Decoding response from OpenAi according to OpenAiResponseSchema..."
            )
            Success(decode[DecodedResponseSchema](responseContent))

          case error =>
            logger.error(s"Failed to create chat completion: $error")
            Success(
              Left(new Exception(s"Failed to create chat completion: $error"))
            )
        }
    }.toEither match {
      case Right(value) => value
      case Left(NonFatal(exception)) =>
        logger.error(
          s"Failed to create chat completion: ${exception.getMessage}"
        )
        Future.successful(Left(exception))
      case Left(fatal) => throw fatal
    }
  }
}
