package com.pithy

import com.pithy.reddit.RedditMainInput

import java.util
import scala.jdk.CollectionConverters.MapHasAsScala

/**
 * The entry point of the program based on platform.
 */
object Launch {

  /**
   * Launches reddit program
   */
  def launchReddit(
    postUrl: String,
    queries: util.Map[String, String]
  ): String = {
    val platformInput: RedditMainInput = new RedditMainInput(
      postUrl
    )

    AppMain.main(
      platformInput,
      Map("queries" -> queries.asScala.toMap)
    ) // .toMap + (s"$otherHash" -> "comments that do not fit into any of the above queryKeys' meanings"))
  }
}
