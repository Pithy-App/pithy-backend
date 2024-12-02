package com.pithy.reddit.requests

/**
 * Reddit API get requests.
 */
sealed trait GetRequestsParams

/**
 * Settings for a comment tree get request.
 * https://www.reddit.com/dev/api/oauth#GET_comments_{article}
 */
case class GetCommentTreeFromPostParams(
  article: String,
  // id36 of a comment. If provided, this comment will be the focal point of the returned view.
  comment: Option[String] = None,
  // context is the number of parents shown for a comment if we set that comment as the focal point
  context: Int = 0,
  // depth is the maximum depth of subtrees in the thread. If None, there's no limit. 1 means only return post. 2 means all top-level comments.
  depth: Option[Int] = Some(2),
  // limit is None for default since we usually want all comments
  limit: Option[Int] = None,
  showEdits: Boolean = false,
  showMedia: Boolean = true,
  showMore: Boolean = false,
  showTitle: Boolean = false,
  sort: String = "",
  // when true, returns details about the subreddit or community in which the post was made. If None or false, these details are omitted.
  sr_detail: Option[Boolean] = Some(true),
  theme: String = "",
  // threaded means to show comments in a nested format else show in a flat format
  threaded: Boolean = true,
  // we're truncating the comment to 500 characters for now
  truncate: Int = 50
) extends GetRequestsParams
