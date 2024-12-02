package com.pithy.auth.reddit

import com.typesafe.config.{Config, ConfigFactory}
import com.pithy.auth.AuthUtils.generate32String

/**
 * Reddit authentication redirect url and global state
 */
case object RedditAuthConfigLoader {
  // Load configuration from application.conf
  private val config: Config = ConfigFactory.load()

  // Reddit configs
  private val clientId: Option[String] = sys.env.get("CLIENT_ID")

  private val clientSecret: Option[String] = sys.env.get("CLIENT_SECRET")

  private val redirectUri: Option[String] = Option(
    config.getString("reddit.redirectUri")
  )

  private val responseType: Option[String] = Option(
    config.getString("reddit.responseType")
  )

  // state to prevent cross-site request forgery (CSRF) attacks
  private val state: String = generate32String

  private val duration: Option[String] = Option(
    config.getString("reddit.duration")
  )

  private val scope: Option[String] = Option(config.getString("reddit.scope"))

  private val redditConfig: RedditConfig =
    (clientId, clientSecret, redirectUri, responseType, duration, scope) match {
      // if all are 6 present, construct a RedditConfig object, else throw an exception of missing configs
      case (Some(_), Some(_), Some(_), Some(_), Some(_), Some(_)) =>
        RedditConfig(
          clientId.get,
          clientSecret.get,
          redirectUri.get,
          responseType.get,
          state,
          duration.get,
          scope.get
        )
      case _ =>
        throw new Exception(
          s"One of Reddit's authentication configs is missing: clientId=$clientId, clientSecret=$clientSecret, redirectUri=$redirectUri, responseType=$responseType, duration=$duration, scope=$scope"
        )
    }

  // these methods have no side effects because previous method has already checked for missing configs
  def getClientId: String = clientId.get

  def getClientSecret: String = clientSecret.get

  def getRedirectUri: String = redirectUri.get

  def getState: String = state

  // Get authentication url, which is to be displayed to users
  def getAuthenticationUrl: String = redditConfig.authUrl
}

/**
 * Reddit api configurations
 *
 * @param clientId Reddit client id
 * @param clientSecret Reddit client secret
 * @param redirectUri Reddit redirect uri
 * @param responseType Reddit response type
 * @param state Reddit state
 * @param duration Reddit duration
 * @param scope Reddit scope
 */

case class RedditConfig(
  private val clientId: String,
  private val clientSecret: String,
  private val redirectUri: String,
  private val responseType: String,
  private val state: String,
  private val duration: String,
  private val scope: String
) {
  val authUrl: String =
    s"https://www.reddit.com/api/v1/authorize?client_id=$clientId&response_type=$responseType&state=$state&redirect_uri=$redirectUri&duration=$duration&scope=$scope"
}
