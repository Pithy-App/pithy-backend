package com.pithy.openai

import com.pithy.PlatformMainOutput
import com.pithy.reddit.RedditMainOutput

/**
 * Input arguments for the main entrypoint of `openai` module
 */
class OpenAIMainInput(
  val platformOutput: PlatformMainOutput,
  val additionalInputs: Map[String, Map[String, String]]
) {

  final val otherHash: Int = "Other".hashCode

  val queries: Map[String, String] =
    additionalInputs.getOrElse("queries", Map.empty)

  def generateGenericPrompt(): String =
    platformOutput match {
      case _: RedditMainOutput =>
        s"""
           |There are ${queries.size + 1} queryKeys to classify the comments of a post into: [\"${queries.keys
            .mkString("\", \"")}\", "$otherHash"]
           |${queries
            .map { case (key, query) => s"queryKey \"$key\" means $query" }
            .mkString("\n")}
           |queryKey "$otherHash" means comments that do not fit into any of the above queryKeys' meanings.
           |To the best of your judgement, please associate each comment with one of the above queryKeys if the queryKey's meaning applies to the comment.
           |""".stripMargin
    }
}

/**
 * Companion object for OpenAIMainInputBuilder
 */
private object OpenAIMainInput {
  def apply(
    platformOutput: PlatformMainOutput,
    additionalInputs: Map[String, Map[String, String]]
  ): OpenAIMainInput =
    new OpenAIMainInput(platformOutput, additionalInputs)
}
