package com.pithy.reddit.requests

/**
 * Utility functions for Reddit API requests.
 */
object RedditUtils {

  // Extract the comment ID from the URL
  def extractCommentId36FromLink(postUrl: String): Either[Throwable, String] = {
    val pattern = ".*/comments/([a-zA-Z0-9]{6,7})/.*".r
    postUrl match {
      case pattern(postId36) => Right(postId36)
      case _ => Left(new Exception(s"Invalid Reddit post URL: $postUrl"))
    }
  }
}
