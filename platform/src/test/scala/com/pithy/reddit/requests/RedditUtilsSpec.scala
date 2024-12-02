package com.pithy.reddit.requests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RedditUtilsSpec extends AnyFlatSpec with RedditUtilsSpecFixture with Matchers {

  "extractCommentId36FromLink" should "extract comment ID1" in {
    RedditUtils.extractCommentId36FromLink(url1) shouldEqual Right("1g086ox")
  }

  it should "extract comment ID2" in {
        RedditUtils.extractCommentId36FromLink(url2) shouldEqual Right("1g19iev")
  }
}
