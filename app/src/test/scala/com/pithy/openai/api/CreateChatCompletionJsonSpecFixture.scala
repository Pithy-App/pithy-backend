package com.pithy.openai.api

import scala.io.{BufferedSource, Source}

trait CreateChatCompletionJsonSpecFixture {

  private val filename =
    "app/src/test/resources/OpenAiResponseTest.txt"

  private val source: BufferedSource = Source.fromFile(filename)
  // Read the entire file as a string
  val response: String = source.mkString

  print(response)

  // Don't forget to close the file when done
  source.close()

  val decodedResponse: DecodedResponseSchema = DecodedResponseSchema(
    statistics = Seq(
      KeyedComment(commentId = 1, queryKey = "agree"),
      KeyedComment(commentId = 8, queryKey = "disagree"),
      KeyedComment(commentId = 9, queryKey = "inconclusive")
    )
  )
}
