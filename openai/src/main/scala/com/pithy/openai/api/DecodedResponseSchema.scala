package com.pithy.openai.api

/**
 * OpenAi's response schema, which is a list of (comment_id, status) pairs.
 * status is of enum type. See ResponseSchemaJson for more details.
 */
case class DecodedResponseSchema(statistics: Seq[KeyedComment])

case class KeyedComment(commentId: Int, queryKey: String)
