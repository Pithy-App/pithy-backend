package com.pithy

/**
 * A utility object to build user inputs.
 */
@deprecated("This object is no longer used.")
object UserInputBuilder {

  /**
   * Represents what user inputs are needed for the program to run.
   *
   * @param postUrl the URL of the post to analyze
   * @param queries: a map of (queryKey -> query) pairs to classify the comments into)
   */
  case class UserInput(
    postUrl: String,
    queries: Map[String, String]
  )

  // The hash code of the "Other" queryKey to avoid conflicts with other user-defined queryKeys
  final val otherHash = "Other".hashCode

  def generateUserInput(
    postUrl: String,
    queryKeys: Map[String, String]
  ): UserInput =
    UserInput(postUrl = postUrl, queries = queryKeys)

  def generateGenericPrompt(userInput: UserInput): String =
    s"""
       |There are ${userInput.queries.size + 1} queryKeys to classify the comments of a post into: [\"${userInput.queries.keys
        .mkString("\", \"")}\", "$otherHash"]
       |${userInput.queries
        .map { case (key, query) => s"queryKey \"$key\" means $query" }
        .mkString("\n")}
       |queryKey "$otherHash" means comments that do not fit into any of the above queryKeys' meanings.
       |To the best of your judgement, please associate each comment with one of the above queryKeys if the queryKey's meaning applies to the comment.
       |""".stripMargin

}

/**
 * Custom user inputs for development.
 */
object FakeUserInput {

  lazy val customUrl1: String =
    "https://www.reddit.com/r/AmItheAsshole/comments/1frxsx5/aita_i_told_him_i_would_never_again_bake_him/?share_id=I0KabK5bQiULmjoDQmvv1&utm_content=1&utm_medium=android_app&utm_name=androidcss&utm_source=share&utm_term=1"

  lazy val customQueries1: Map[String, String] = Map(
    "support" -> "comments that think OP is an asshole",
    "oppose" -> "comments that think OP is not an asshole",
    "inconclusive" -> "comments that are either irrelevant, or do not express a clear supporting or opposing opinion."
  )

  lazy val customUrl2: String =
    "https://www.reddit.com/r/Cornell/comments/1g1dgfx/most_underrated_vegetable/"
  lazy val customQueries2: Map[String, String] = Map(
    "bokchoy" -> "comments that think bokchoy is the most underrated vegetable",
    "cabbage" -> "comments that think cabbage is the most underrated vegetable"
  )

  lazy val customUrl3: String =
    "https://www.reddit.com/r/TeslaLounge/comments/1g086ox/teslas_we_robot_robotaxi_event_megathread/"
  lazy val customQueries3: Map[String, String] = Map(
    "support" -> "comments that think Tesla's new products are awesome",
    "oppose" -> "comments that think views Tesla's new products negatively",
    "inconclusive" -> "comments that are either irrelevant, or do not express a clear supporting or opposing opinion."
  )
}
