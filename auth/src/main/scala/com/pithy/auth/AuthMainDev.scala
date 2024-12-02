package com.pithy.auth

/**
 * Main entry point for the authentication process if run "auth" locally.
 * This is useful for testing the authentication locally.
 */
object AuthMainDev {
  def main(args: Array[String]): Unit = {
    val platform = "reddit"
    val result = AuthMain.main(platform)
    print(result)
  }
}
