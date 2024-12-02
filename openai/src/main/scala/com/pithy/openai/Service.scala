package com.pithy.openai

import akka.actor.ActorSystem
import com.pithy.openai.api.{DecodedResponseSchema, RequestMessageSchema}
import com.pithy.openai.CreateChatCompletionJson.logger
import io.cequence.openaiscala.domain.response.ChatCompletionResponse
import io.cequence.openaiscala.domain.settings.CreateChatCompletionSettings
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}
import io.cequence.wsclient.service.CloseableService
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

/**
 * OpenAI Service Interface for creating and running a chat completion
 */
trait Service extends ServiceBase[OpenAIService] {
  override protected val service: OpenAIService = OpenAIServiceFactory()
}

trait ServiceBase[T <: CloseableService] {

  implicit lazy val system: ActorSystem = ActorSystem("openai-system")
  implicit lazy val executionContext: ExecutionContextExecutor =
    system.dispatcher

  protected val service: T

  // Creates and runs request with the given message
  def createAndRun(
    message: RequestMessageSchema,
    settings: CreateChatCompletionSettings
  ): Future[Either[Throwable, DecodedResponseSchema]] =
    run(message, settings)
      .transform {
        case Success(value) =>
          closeAll() // Close resources when successful
          Success(value)

        case Failure(exception) =>
          exception.printStackTrace() // Log the exception
          closeAll() // Close resources in case of error
          System.exit(1) // Exit with non-zero status
          Failure(exception) // Still return the Failure to the caller
      }

  //      .map { response =>
  //        response // Successfully decode the response
  //      }
  //      .recover { case e: Throwable =>
  //        logger.error(
  //          s"Failed to create and run chat completion: ${e.getMessage}"
  //        )
  //        Left(e)
  //      }
  //      .andThen { case _ => closeAll() }

  private def closeAll(): Unit =
    //    service.close()
    system.terminate()

  protected def run(
    message: RequestMessageSchema,
    settings: CreateChatCompletionSettings
  ): Future[Either[Throwable, DecodedResponseSchema]]

  protected def printMessageContent(response: ChatCompletionResponse): Unit =
    println(response.choices.head.message.content)

  protected def messageContent(response: ChatCompletionResponse): String =
    response.choices.head.message.content
}
