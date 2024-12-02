package com.pithy.auth

import com.pithy.auth.reddit.RetrieveCode.retrieveCode
import com.pithy.auth.reddit.RedditAuthConfigLoader
import com.pithy.auth.reddit.RetrieveToken.authenticate
import com.pithy.shared.LambdaEvents

object AuthMain {
  def main(platform: String): String = {
    // get authentication url
    val authUrl: String = platform match {
      case "reddit" => RedditAuthConfigLoader.getAuthenticationUrl
      case invalid  => throw new Exception(s"Invalid platform: $invalid")
    }

    // TODO PITH-9: remove println statement when UI can display the url
    println(s"The authentication url is $authUrl")

    // retrieve token after user has authenticated
    val oneTimeCode: String = retrieveCode() match {
      case Right(code) => code
      case Left(error) => throw error
    }

    println(s"one time code is $oneTimeCode")

    // Load environment configurations
    val clientId: String = RedditAuthConfigLoader.getClientId
    val clientSecret: String = RedditAuthConfigLoader.getClientSecret

    // Retrieve token using the one time code, client id, and client secret
    val tokenResponse: String =
      authenticate(oneTimeCode, clientId, clientSecret) match {
        case Right(response) => response.toString
        case Left(error)     => throw error
      }

    tokenResponse
  }

  /**
   * Main entry point for the platform authentication process upon user request
   */
  def main_v2(
    platform: String,
    event: LambdaEvents
  ): Either[Throwable, String] =
    platform match {
      case "reddit" =>
        val code: String = event.getRedditAuthCode
        val clientId: String = RedditAuthConfigLoader.getClientId
        val clientSecret: String = RedditAuthConfigLoader.getClientSecret

        authenticate(code, clientId, clientSecret) match {
          case Right(response) => Right(response.toString)
          case Left(error)     => Left(error)
        }

      case _ =>
        Left(new Exception(s"Invalid platform: $platform"))
    }
}
