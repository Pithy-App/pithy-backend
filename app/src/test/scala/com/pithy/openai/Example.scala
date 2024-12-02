package com.pithy.openai

import akka.actor.ActorSystem
import io.cequence.openaiscala.domain.response.ChatCompletionResponse
import io.cequence.openaiscala.service.{OpenAIService, OpenAIServiceFactory}
import io.cequence.wsclient.service.CloseableService

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * An example interface of using the OpenAI API
 */
trait Example extends ExampleBase[OpenAIService] {
  override protected val service: OpenAIService = OpenAIServiceFactory()
}

trait ExampleBase[T <: CloseableService] {

  lazy implicit val system: ActorSystem = ActorSystem("openai-system-test")
  lazy implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  protected val service: T

  def createAndRun(args: Array[String]): Unit =
    run
      .recover { case e: Exception =>
        e.printStackTrace()
        closeAll()
        System.exit(1)
      }
      .onComplete { _ =>
        closeAll()
        System.exit(0)
      }

  private def closeAll() = {
    service.close()
    system.terminate()
  }

  protected def run: Future[_]

  protected def printMessageContent(response: ChatCompletionResponse): Unit =
    println(response.choices.head.message.content)

  protected def messageContent(response: ChatCompletionResponse): String =
    response.choices.head.message.content
}
