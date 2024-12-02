package com.pithy.reddit.responses

import io.circe._
import io.circe.generic.semiauto._

/**
 * Case class for a Reddit listing
 */
case class Listing(kind: Option[String], data: ListingData)

/**
 * Case class for a Reddit listing's data field
 */
case class ListingData(
  after: Option[String],
  dist: Option[Int],
  modhash: Option[String],
  geo_filter: Option[String],
  children: List[Child],
  before: Option[String]
)

/**
 * Reddit case class for each item in the children array
 */
case class Child(kind: Option[String], data: ChildData)

/** Reddit case class for the "data" inside each child (which could be a post or a comment) */
case class ChildData(
  body: Option[String],
  approved_at_utc: Option[Double],
  subreddit: Option[String],
  selftext: Option[String],
  user_reports: Option[List[String]],
  saved: Option[Boolean],
  mod_reason_title: Option[String],
  gilded: Option[Int],
  clicked: Option[Boolean],
  title: Option[String],
  link_flair_richtext: Option[List[Map[String, String]]],
  subreddit_name_prefixed: Option[String],
  hidden: Option[Boolean],
  pwls: Option[Int],
  link_flair_css_class: Option[String],
  downs: Option[Int],
  thumbnail_height: Option[Int],
  top_awarded_type: Option[String],
  parent_whitelist_status: Option[String],
  hide_score: Option[Boolean],
  name: Option[String],
  quarantine: Option[Boolean],
  link_flair_text_color: Option[String],
  upvote_ratio: Option[Double],
  author_flair_background_color: Option[String],
  subreddit_type: Option[String],
  ups: Option[Int],
  total_awards_received: Option[Int],
  media_embed: Option[MediaEmbed],
  thumbnail_width: Option[Int],
  author_flair_template_id: Option[String],
  is_original_content: Option[Boolean],
  author_fullname: Option[String],
  secure_media: Option[String],
  is_reddit_media_domain: Option[Boolean],
  is_meta: Option[Boolean],
  category: Option[String],
  secure_media_embed: Option[MediaEmbed],
  link_flair_text: Option[String],
  can_mod_post: Option[Boolean],
  score: Option[Int],
  approved_by: Option[String],
  is_created_from_ads_ui: Option[Boolean],
  author_premium: Option[Boolean],
  thumbnail: Option[String],
  author_flair_css_class: Option[String],
  author_flair_richtext: Option[List[Option[FlairRichText]]],
  gildings: Option[Gildings],
  post_hint: Option[String],
  content_categories: Option[String],
  is_self: Option[Boolean],
  mod_note: Option[String],
  created: Option[Double],
  link_flair_type: Option[String],
  wls: Option[Int],
  removed_by_category: Option[String],
  banned_by: Option[String],
  author_flair_type: Option[String],
  domain: Option[String],
  allow_live_comments: Option[Boolean],
  selftext_html: Option[String],
  likes: Option[String],
  suggested_sort: Option[String],
  banned_at_utc: Option[String],
  url_overridden_by_dest: Option[String],
  view_count: Option[String],
  archived: Option[Boolean],
  no_follow: Option[Boolean],
  is_crosspostable: Option[Boolean],
  pinned: Option[Boolean],
  over_18: Option[Boolean],
  preview: Option[Preview],
  all_awardings: Option[List[String]],
  awarders: Option[List[String]],
  media_only: Option[Boolean],
  can_gild: Option[Boolean],
  spoiler: Option[Boolean],
  locked: Option[Boolean],
  author_flair_text: Option[String],
  treatment_tags: Option[List[String]],
  visited: Option[Boolean],
  removed_by: Option[String],
  num_reports: Option[Int],
  distinguished: Option[String],
  subreddit_id: Option[String],
  author_is_blocked: Option[Boolean],
  mod_reason_by: Option[String],
  removal_reason: Option[String],
  link_flair_background_color: Option[String],
  id: Option[String],
  is_robot_indexable: Option[Boolean],
  num_duplicates: Option[Int],
  report_reasons: Option[String],
  author: Option[String],
  discussion_type: Option[String],
  num_comments: Option[Int],
  send_replies: Option[Boolean],
  media: Option[String],
  contest_mode: Option[Boolean],
  author_patreon_flair: Option[Boolean],
  author_flair_text_color: Option[String],
  permalink: Option[String],
  whitelist_status: Option[String],
  stickied: Option[Boolean],
  url: Option[String],
  subreddit_subscribers: Option[Int],
  created_utc: Option[Double],
  num_crossposts: Option[Int],
  mod_reports: Option[List[String]],
  is_video: Option[Boolean],
  replies: Option[Replies]
)

// Additional nested case classes
case class Replies(listing: Either[String, Listing])
case class MediaEmbed()
case class Gildings()
case class Preview(images: List[Image], enabled: Boolean)
case class Image(
  source: ImageSource,
  resolutions: List[Resolution],
  id: String
)
case class ImageSource(url: String, width: Int, height: Int)
case class Resolution(url: String, width: Int, height: Int)

case class FlairRichText(e: Option[String], t: Option[String])

/**
 * custom decoders under `Listing` object
 */
object Listing {
  implicit val repliesDecoder: Decoder[Replies] = (c: HCursor) =>
    c.focus match {
      case Some(json) if json.isString =>
        c.as[String].map(str => Replies(Left(str))) // Decode as Left[String]
      case Some(json) if json.isObject =>
        c.as[Listing]
          .map(listing => Replies(Right(listing))) // Decode as Right[Listing]
      case _ =>
        Left(
          DecodingFailure("Expected String or Listing", c.history)
        )
    }
  implicit val listingDecoder: Decoder[Listing] = deriveDecoder[Listing]
  implicit val listingDataDecoder: Decoder[ListingData] =
    deriveDecoder[ListingData]
  implicit val childDecoder: Decoder[Child] = deriveDecoder[Child]
  implicit val childDataDecoder: Decoder[ChildData] = deriveDecoder[ChildData]
  implicit val mediaEmbedDecoder: Decoder[MediaEmbed] =
    deriveDecoder[MediaEmbed]
  implicit val gildingsDecoder: Decoder[Gildings] = deriveDecoder[Gildings]
  implicit val previewDecoder: Decoder[Preview] = deriveDecoder[Preview]
  implicit val imageDecoder: Decoder[Image] = deriveDecoder[Image]
  implicit val imageSourceDecoder: Decoder[ImageSource] =
    deriveDecoder[ImageSource]
  implicit val resolutionDecoder: Decoder[Resolution] =
    deriveDecoder[Resolution]
  implicit val flairRichTextDecoder: Decoder[FlairRichText] =
    deriveDecoder[FlairRichText]
}
