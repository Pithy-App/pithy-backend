package com.pithy.openai.api

import com.pithy.reddit.responses.Listing
import com.pithy.openai.OpenAIMainInput
import io.circe.Json
import io.circe.Printer.spaces2
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

import scala.util.chaining.scalaUtilChainingOps

/**
 * Analytics Object for the OpenAI response.
 * It contains basic precalculated analytics for the response, and utility methods to further analyze the response.
 */

class ResponseAnalyzer(
  response: DecodedResponseSchema,
  redditResponse: List[Listing],
  userInput: OpenAIMainInput
) {
  val length: Int = response.statistics.length
  private val queryKeyRatio: Map[String, Double] =
    calculateRatio(response, userInput)
  private val labeledComments: Seq[(Int, String, String)] =
    pairCommentsWithQueryKey(
      response,
      redditResponse,
      userInput,
      length
    )

  // Calculate the ratio of each queryKey in the response
  // The ratio is the number of comments with the queryKey divided by the total number of comments
  private def calculateRatio(
    response: DecodedResponseSchema,
    userInput: OpenAIMainInput
  ): Map[String, Double] = {
    val commentCount = response.statistics.length

    val queryKeys = userInput.queries.keys

    queryKeys.map { queryKey =>
      val queryKeyCount = response.statistics.count(_.queryKey == queryKey)
      val ratio = queryKeyCount.toDouble / commentCount

      // Convert otherHash back to "Other" or "Others" for display
      if (
        queryKey == userInput.otherHash.toString && !queryKeys.toSet.contains(
          "Other"
        )
      ) {
        "Other" -> ratio
      } else if (queryKey == userInput.otherHash.toString) {
        "Others" -> ratio
      } else {
        queryKey -> ratio
      }
    }.toMap
  }

  // Pair the first (possibly randomly chosen) N comments' ID, queryKey with the actual comment text
  // Returns: List of (comment_id, queryKey, comment_text)
  private def pairCommentsWithQueryKey(
    openAiResponse: DecodedResponseSchema,
    redditResponse: List[Listing],
    openAIMainInput: OpenAIMainInput,
    top: Int = 0
  ): Seq[(Int, String, String)] = {
    val redditCommentList = redditResponse.tail.head.data.children.map {
      child =>
        child.data.body.get
    }

    val openAiCommentList = openAiResponse.statistics.map { comment =>
      if (comment.queryKey != openAIMainInput.otherHash.toString)
        (comment.commentId, comment.queryKey)
      else (comment.commentId, "Other")
    }

    openAiCommentList
      .zip(redditCommentList)
      .map(nestedTuple =>
        (nestedTuple._1._1, nestedTuple._1._2, nestedTuple._2)
      )
      .take(if (top > 0) top else 0)
  }

  /**
   * Return the collection of comments that belongs to the queryKey given a queryKey
   */
  private def getCommentsByQueryKey(queryKey: String): Seq[(Int, String)] =
    labeledComments
      .filter(_._2 == queryKey)
      .map(pair => (pair._1, pair._3))

  /**
   * Comment object for JSON serialization for openai output. Platform is reddit.
   * @param id: comment ID
   * @param text: comment text
   */
  private case class Comment(id: String, text: String)

  /**
   * CommentWithRatio object for JSON serialization for openai output. Platform is reddit.
   * @param ratio: ratio of the queryKey in the response
   * @param comments: list of Comment object
   */
  private case class CommentWithRatio(ratio: Double, comments: Seq[Comment])

  /**
   * toPieChartJson outputs required information for pie chart display
   *
   * Example output:
   * {
   *  queryKey1:
   *  {
   *    ratio: 0.5,
   *    comments: [
   *    {id: 1, text: "comment1"},
   *    {id: 2, text: "comment2"}
   *    ]
   *   },
   *   queryKey2: {
   *   ...
   *   }
   *  }
   */
  def toPieChartJson: String = {

//    val res = queryKeyRatio.toSeq
//      .map { case (queryKey, ratio) =>
//        Map(
//          queryKey -> Map(
//            "ratio" -> ratio.asJson,
//            "comments" -> getCommentsByQueryKey(queryKey).map {
//              case (id, text) =>
//                Map("id" -> id, "text" -> text).asJson
//            }
//          ).asJson
//        ).asJson
//      }
    val res = queryKeyRatio.toSeq
      .map { case (queryKey, ratio) =>
        Map(
          queryKey -> CommentWithRatio(
            ratio,
            getCommentsByQueryKey(queryKey).map { case (id, text) =>
              Comment(id.toString, text)
            }
          )
        )
      }

    res.asJson.spaces2
  }
}
