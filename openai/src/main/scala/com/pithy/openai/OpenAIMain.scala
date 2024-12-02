package com.pithy.openai

import com.pithy.openai.api.CustomSettings.createCustomJsonChatCompletion
import com.pithy.openai.api.ResponseSchemaJsonBuilder.generateResponseSchemaJsonFromPlatform
import com.pithy.openai.api.{DecodedResponseSchema, RequestMessageSchema}
import com.pithy.reddit.RedditMainOutput
import io.cequence.openaiscala.domain.{BaseMessage, ModelId, SystemMessage}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

object OpenAIMain {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def main(
    input: OpenAIMainInput
  ): Future[Either[Throwable, DecodedResponseSchema]] =
    input.platformOutput match {
      case platformOutput: RedditMainOutput =>
        val messagesToOpenAi: Seq[BaseMessage] =
          platformOutput.output.toOpenAiMessage match {
            case Right(messages) => messages
            case Left(failure)   => throw failure
          }

        // calculate max number of tokens that should be capped for OpenAI's response to prevent bankruptcy
        val messageToOpenAiLength: Int = messagesToOpenAi.length

        val queries: Map[String, String] = input.queries
        logger.info("Got reddit queries: {} for openAI", queries)

        val recommendedMaxTokens: Int =
          ("""{"commentId":__,"queryKey":""}""" + queries.maxBy(
            _._1
          )).length * messageToOpenAiLength

        // Interaction with OpenAI API starts here ========================================

        val settings = createCustomJsonChatCompletion(
          model = ModelId.gpt_4o_mini_2024_07_18,
          maxTokens = recommendedMaxTokens,
          jsonSchema = generateResponseSchemaJsonFromPlatform(input)
        )

        val prompt: String = input.generateGenericPrompt()

        val messagesToOpenAiWithPrompt: Seq[BaseMessage] =
          SystemMessage(prompt) +: messagesToOpenAi

        CreateChatCompletionJson.createAndRun(
          message = RequestMessageSchema(messagesToOpenAiWithPrompt),
          settings = settings
        )

      case _ =>
        throw new IllegalArgumentException(
          "Unsupported platform. Currently supported platforms: reddit"
        )
    }
}
