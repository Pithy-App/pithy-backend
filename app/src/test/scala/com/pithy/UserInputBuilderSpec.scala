package com.pithy

import com.pithy.UserInputBuilder.otherHash
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
class UserInputBuilderSpec extends AnyFlatSpec with UserInputBuilderSpecFixture with Matchers {
  "generateGenericPrompt()" should "generate a good prompt" in {
    val userInput = UserInputBuilder.generateUserInput(postUrl, queries)
    val prompt = UserInputBuilder.generateGenericPrompt(userInput)
    val expectedPrompt =
      s"""
        |There are 3 queryKeys to classify the comments of a post into: ["bokchoy", "cabbage", "$otherHash"]
        |queryKey "bokchoy" means comments that think bokchoy is the most underrated vegetable
        |queryKey "cabbage" means comments that think cabbage is the most underrated vegetable
        |queryKey "$otherHash" means comments that do not fit into any of the above queryKeys' meanings.
        |To the best of your judgement, please associate each comment with one of the above queryKeys if the queryKey's meaning applies to the comment.
        |""".stripMargin

    prompt should equal(expectedPrompt)

  }
}
