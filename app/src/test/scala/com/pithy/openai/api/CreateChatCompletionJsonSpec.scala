package com.pithy.openai.api

import org.scalatest.funsuite.AnyFunSuite
import io.circe.generic.auto._
import io.circe.parser._

class CreateChatCompletionJsonSpec extends AnyFunSuite with CreateChatCompletionJsonSpecFixture {

  test("Example OpenAi response should be decoded as `DecodedResponseSchema`") {
    assert(decode[DecodedResponseSchema](response) == Right(decodedResponse))
  }

}
