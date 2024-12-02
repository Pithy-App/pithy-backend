package com.pithy.reddit

import com.pithy.PlatformMainInput

/**
 * Input format required to initiate process with Reddit platform
 */
class RedditMainInput(val postUrl: String) extends PlatformMainInput {
  override val platform = "reddit"
}
