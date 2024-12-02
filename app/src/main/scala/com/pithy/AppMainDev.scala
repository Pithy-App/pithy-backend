package com.pithy

import com.pithy.openai.OpenAIMainInput
import com.pithy.shared.FakeInputs
import com.pithy.reddit.RedditMainInput
import com.pithy.shared.FakeInputs.{FakeOpenAIInput, FakeRedditInput}

/**
 * Main entry point for the application if run locally.
 * This is useful for testing the application locally.
 */
object AppMainDev {
  def main(args: Array[String]): Unit = {

    // create fake input
    val platformInput: PlatformMainInput = new RedditMainInput(
      FakeRedditInput.customUrl1
    )

    val additionalInputs = Map(
      "queries" ->
        FakeOpenAIInput.FakeRedditQueries.customQueries1
    )

    val result = AppMain.main(
      platformInput = platformInput,
      additionalInputs = additionalInputs,
      prod = false
    )
    print(result)
  }
}
