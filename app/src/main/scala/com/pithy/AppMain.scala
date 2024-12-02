package com.pithy

import com.pithy.openai.{CreateChatCompletionJson, OpenAIMain, OpenAIMainInput}
import com.pithy.openai.api.{RequestMessageSchema, ResponseAnalyzer}
import com.pithy.openai.api.ResponseSchemaJsonBuilder.generateResponseSchemaJsonFromPlatform
import com.pithy.reddit.{RedditMain, RedditMainInput}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.math.BigDecimal.{double2bigDecimal, RoundingMode}
import scala.util.chaining.scalaUtilChainingOps

object AppMain {
  def main(
    platformInput: PlatformMainInput,
    additionalInputs: Map[String, Map[String, String]],
    prod: Boolean = true
  ): String =
    platformInput match {
      case input: RedditMainInput =>
        val redditOutput = RedditMain.main(input, prod)
        val decodedRedditResponse = redditOutput.output.decodedResponse

        val openAIInput: OpenAIMainInput = new OpenAIMainInput(
          redditOutput,
          additionalInputs
        )
        val openAiResponseFuture = OpenAIMain.main(openAIInput)

        // wait 2 min for the response from OpenAI
        Await.result(openAiResponseFuture, 2.minute) match {
          case Right(openAiResponse) =>
            val responseAnalyzer = new ResponseAnalyzer(
              openAiResponse,
              decodedRedditResponse,
              openAIInput
            )

            responseAnalyzer.toPieChartJson

          case Left(failure) =>
            throw failure
        }

      case _ =>
        "Support for other platforms is to be implemented" // TODO: Support other platforms
    }
}
