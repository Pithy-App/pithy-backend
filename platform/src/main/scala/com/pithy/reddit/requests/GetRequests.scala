package com.pithy.reddit.requests

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri, headers}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.pithy.errors.{GeneralError, HttpError, RequestError, TimeoutError, UnmarshalError}
import org.slf4j.{Logger, LoggerFactory}

import java.util.concurrent.TimeoutException
import scala.concurrent.ExecutionContextExecutor
import scala.util.control.NonFatal

/**
 * Reddit API get requests' shared interface
 */
trait GetRequests {
  // Make the GET request
  def send(): Future[Either[RequestError, String]]

  // Actor system and execution context
  implicit val system: ActorSystem
  implicit val executionContext: ExecutionContextExecutor

  protected val logger: Logger = LoggerFactory.getLogger(getClass)

  /**
   * Make a given request
   */
  def makeRequest(request: HttpRequest): Future[Either[RequestError, String]] =
    Http()
      .singleRequest(request)
      .flatMap {
        case response if response.status.isSuccess() =>
          Unmarshal(response.entity).to[String].map(Right(_)).recover {
            case NonFatal(e) =>
              Left(
                UnmarshalError(s"Failed to unmarshal response: ${e.getMessage}")
              )
          }
        case response =>
          Future.successful(
            Left(
              HttpError(
                response.status.intValue(),
                s"HTTP error: ${response.status}, ${response.status.reason()}, from request ${HttpRequest.toString}"
              )
            )
          )
      }
      .recover {
        case _: TimeoutException =>
          Left(
            TimeoutError(
              s"Request timed out during HTTP call: ${HttpRequest.toString}"
            )
          )
        case NonFatal(e) =>
          Left(
            GeneralError(
              s"Failed to make request with the following non-fatal error: ${e.getMessage}"
            )
          )
      }
}

/**
 * Get a Reddit post's comment tree.
 * https://www.reddit.com/dev/api/oauth#GET_comments_{article}
 */
class GetCommentTreeFromPost(
  val token: String,
  val params: GetCommentTreeFromPostParams
)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContextExecutor
) extends GetRequests {

  // Method to make the GET to retrieve the comment tree
  override def send(): Future[Either[RequestError, String]] = {
    // Base URI for Reddit comments
    val baseUri = Uri(s"https://oauth.reddit.com/comments/${params.article}")

    // Building query parameters dynamically
    val queryParams = Query(
      params.comment.map("comment" -> _).toList ++
        Some("context" -> params.context.toString) ++
        params.depth.map("depth" -> _.toString).toList ++
        params.limit.map("limit" -> _.toString).toList ++
        Some("showedits" -> params.showEdits.toString) ++
        Some("showmedia" -> params.showMedia.toString) ++
        Some("showmore" -> params.showMore.toString) ++
        Some("showtitle" -> params.showTitle.toString) ++
        Some("sort" -> params.sort) ++
        params.sr_detail.map("sr_detail" -> _.toString).toList ++
        Some("theme" -> params.theme) ++
        Some("threaded" -> params.threaded.toString) ++
        Some("truncate" -> params.truncate.toString): _*
    )

    // Constructing the HTTP GET request with Authorization header
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = baseUri.withQuery(queryParams),
      headers = List(headers.Authorization(OAuth2BearerToken(token)))
    )

    // Sending the HTTP request
    logger.info("Sending GetCommentTreeFromPostHTTP request to Reddit API...")
    makeRequest(request)
  }

//  // Await the future response synchronously
//  override val response: Either[Throwable, String] = Try {
//    logger.info("Sending HTTP request to Reddit API...")
//    Await.result(send(), 30.second)
//  } match {
//    case Success(result) =>
//      logger.info(s"Retrieved response: ${result.substring(0, 100)}...")
//      Right(result)
//    case Failure(exception: TimeoutException) =>
//      // Handle the timeout (e.g., return a default value, retry, etc.)
//      Left(
//        new TimeoutException(
//          s"Get comment tree of post timed out: ${exception.getMessage}"
//        )
//      )
//    case Failure(exception) =>
//      // Handle other types of exceptions
//      Left(
//        new Exception(
//          s"An error occurred when getting comment tree of post: ${exception.getMessage}"
//        )
//      )
//  }

}

/**
 * Get the identifier of a Reddit user.
 */
class GetUserId(val token: String)(
  implicit val system: ActorSystem,
  implicit val executionContext: ExecutionContextExecutor
) extends GetRequests {

  override def send(): Future[Either[RequestError, String]] = {

    // request to get the user's id
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = "/api/v1/me",
      headers = List(headers.Authorization(OAuth2BearerToken(token)))
    )

    // send the request
    logger.info("Sending \"Get user Id\" HTTP request to Reddit...")
    makeRequest(request)
  }
}
