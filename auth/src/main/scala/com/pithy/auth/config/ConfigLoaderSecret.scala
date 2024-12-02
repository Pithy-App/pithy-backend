package com.pithy.auth.config

/**
 * Load environment configurations for authentication
 */
object ConfigLoaderSecret {

  /**
   * Load configurations for Reddit authentication
   */
  object RedditConfig {
    private val clientId: Option[String] = sys.env.get("CLIENT_ID")
    private val clientSecret: Option[String] = sys.env.get("CLIENT_SECRET")

    def getClientId: Option[String] = clientId
    def getClientSecret: Option[String] = clientSecret
  }
}
