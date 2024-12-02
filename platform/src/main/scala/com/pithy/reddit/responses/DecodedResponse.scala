package com.pithy.reddit.responses

import com.pithy.PlatformResponseBuilder
import io.cequence.openaiscala.domain.{BaseMessage, UserMessage}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Interface for converting Reddit Json string response to an intermediary format
 * The intermediary format is used to
 *  1. construct OpenAI messages
 *  2. upload to Postgres
 *  3. combine with OpenAI response to form meaningful interpretation for end users
 */
sealed trait DecodedResponse {
  val decodedResponse: List[Listing]
  def toOpenAiMessage: Either[Throwable, Seq[BaseMessage]]

  // TODO: implement this method when uploading to Postgres
  def toPostgreSql(): Unit

  // TODO: implement this method when combining Reddit response with OpenAI response to form meaningful interpretation
  def combineAndInterpret(): Unit
}

/**
 * The decoded response of the `GetCommentTreeFromPost` api request.
 */
class DecodedGetCommentTreeFromPostBuilder(
  val decodedResponse: List[Listing]
) extends PlatformResponseBuilder {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  override def toOpenAiMessage: Either[Throwable, Seq[BaseMessage]] = {
    logger.info("Extracting post title")
    val maybePostTitle: Option[BaseMessage] =
      decodedResponse.head.data.children.head.data.title
        .map { body =>
          UserMessage("The post's title is:\n" + body)
        }

    logger.info("Extracting post body")
    val postBody: BaseMessage =
      decodedResponse.head.data.children.head.data.body
        .map { body =>
          UserMessage("The post's body is:\n" + body)
        }
        .getOrElse(UserMessage("The post has no body."))

    logger.info("Extracting comment bodies")
    val maybeCommentBodies =
      decodedResponse.tail.head.data.children.zipWithIndex
        .map { case (comment, index) =>
          comment.data.body match {
            case Some(body) => UserMessage(s"Comment ${index + 1}:\n" + body)
            case None       => UserMessage(s"Comment ${index + 1} has no body.")
          }
        }

    maybePostTitle match {
      case Some(postTitle) =>
        Right(Seq(postTitle, postBody) ++ maybeCommentBodies)
      case None => Left(new Exception("Failed to retrieve post information"))
    }
  }
}
