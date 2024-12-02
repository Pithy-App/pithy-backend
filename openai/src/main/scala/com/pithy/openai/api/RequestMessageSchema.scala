package com.pithy.openai.api

import io.cequence.openaiscala.domain.BaseMessage

/**
 * The message of an OpenAI chat completion request.
 */
case class RequestMessageSchema(content: Seq[BaseMessage])
