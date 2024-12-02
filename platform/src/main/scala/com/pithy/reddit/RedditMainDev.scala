package com.pithy.reddit

import com.pithy.shared.FakeInputs.FakeRedditInput

/**
 * Main entry point for the reddit platform if run "platform" locally.
 */
object RedditMainDev {
  def main(args: Array[String]): Unit = {

    // create fake input
    val input: RedditMainInput = new RedditMainInput(FakeRedditInput.customUrl)

    val result: RedditMainOutput = RedditMain.main(input, prod = false)
    print(result)
  }
}
