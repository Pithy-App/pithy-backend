package com.pithy

trait UserInputBuilderSpecFixture {
  val postUrl: String = "https://www.reddit.com/r/Cornell/comments/1g1dgfx/most_underrated_vegetable/"
  val queries: Map[String, String] = Map(
    "bokchoy" -> "comments that think bokchoy is the most underrated vegetable",
    "cabbage" -> "comments that think cabbage is the most underrated vegetable"
  )
}
