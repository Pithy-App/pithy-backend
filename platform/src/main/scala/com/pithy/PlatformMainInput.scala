package com.pithy

/**
 * Input arguments for the main entrypoint of `platform` module
 */
trait PlatformMainInput {
  val platform: String
}

/**
 * Input format required to initiate process with Reddit platform
 */

//object RedditMainInputBuilder extends PlatformMainInput {
//
//  final val otherHash: Int = "Other".hashCode
//  case class RedditMainInput(postUrl: String, queries: Map[String, String])
//      extends PlatformMainInput
//  def apply(postUrl: String, queries: Map[String, String]): RedditMainInput =
//    RedditMainInput(postUrl, queries)
//
//  def generateGenericPrompt(input: RedditMainInput): String =
//    s"""
//       |There are ${input.queries.size + 1} queryKeys to classify the comments of a post into: [\"${input.queries.keys
//        .mkString("\", \"")}\", "$otherHash"]
//       |${input.queries
//        .map { case (key, query) => s"queryKey \"$key\" means $query" }
//        .mkString("\n")}
//       |queryKey "$otherHash" means comments that do not fit into any of the above queryKeys' meanings.
//       |To the best of your judgement, please associate each comment with one of the above queryKeys if the queryKey's meaning applies to the comment.
//       |""".stripMargin
//}
