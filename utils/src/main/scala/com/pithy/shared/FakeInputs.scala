package com.pithy.shared

/**
 * Fake inputs for some modules for testing purposes
 */
object FakeInputs {
  object FakeRedditInput {
    lazy val customUrl: String =
      "https://www.reddit.com/r/Cornell/comments/1g6chzq/this_doesnt_stop_with_taal_university_bans_four/"
    lazy val customUrl1: String =
      "https://www.reddit.com/r/AmItheAsshole/comments/1frxsx5/aita_i_told_him_i_would_never_again_bake_him/?share_id=I0KabK5bQiULmjoDQmvv1&utm_content=1&utm_medium=android_app&utm_name=androidcss&utm_source=share&utm_term=1"
    lazy val customUrl2: String =
      "https://www.reddit.com/r/Cornell/comments/1g1dgfx/most_underrated_vegetable/"
    lazy val customUrl3: String =
      "https://www.reddit.com/r/TeslaLounge/comments/1g086ox/teslas_we_robot_robotaxi_event_megathread/"
  }

  object FakeOpenAIInput {
    object FakeRedditQueries {
      lazy val customQueries1: Map[String, String] = Map(
        "support" -> "comments that think OP is an asshole",
        "oppose" -> "comments that think OP is not an asshole",
        "inconclusive" -> "comments that are either irrelevant, or do not express a clear supporting or opposing opinion."
      )

      lazy val customQueries2: Map[String, String] = Map(
        "bokchoy" -> "comments that think bokchoy is the most underrated vegetable",
        "cabbage" -> "comments that think cabbage is the most underrated vegetable"
      )

      lazy val customQueries3: Map[String, String] = Map(
        "support" -> "comments that think Tesla's new products are awesome",
        "oppose" -> "comments that think views Tesla's new products negatively",
        "inconclusive" -> "comments that are either irrelevant, or do not express a clear supporting or opposing opinion."
      )
    }
  }
}
