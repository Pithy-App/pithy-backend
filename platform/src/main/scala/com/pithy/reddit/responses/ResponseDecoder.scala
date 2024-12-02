package com.pithy.reddit.responses
import com.pithy.errors.{AppError, RedditResponseDecodeError}
import io.circe.generic.auto._
import io.circe.parser._
import org.slf4j.{Logger, LoggerFactory}

/**
 * Decodes the response from the Reddit API and converts it into a Scala case class
 * using one or more of "/platform/reddit/schemas/RedditApiResponseSchemas".
 */
object ResponseDecoder {
  val logger: Logger = LoggerFactory.getLogger(getClass)
  def decodeResponseGetCommentTreeFromPost(
    response: String
  ): Either[AppError, DecodedGetCommentTreeFromPostBuilder] = {

    logger.info(
      "Decoding response from Reddit API - turning it into one or more Scala case classes"
    )
    val decodedResponse: Either[Throwable, List[Listing]] =
      decode[List[Listing]](response)

    logger.info("Constructing DecodedGetCommentTreeFromPost object")
    decodedResponse match {
      case Left(error) =>
        logger.error("Failed to construct DecodedGetCommentTreeFromPost object")
        Left(
          RedditResponseDecodeError(
            s"Failed to decode Reddit response: ${error.getMessage}"
          )
        )
      case Right(listing) =>
        logger.info("Almost constructed DecodedGetCommentTreeFromPost object")
        Right(new DecodedGetCommentTreeFromPostBuilder(listing))
    }
  }
}
