package com.pithy.openai.api

import io.cequence.openaiscala.domain
import io.cequence.openaiscala.domain.ModelId
import io.cequence.openaiscala.domain.settings.{
  ChatCompletionResponseFormatType,
  CreateChatCompletionSettings,
  JsonSchemaDef
}

/**
 * Custom settings for making an OpenAI API call, if do not want to use DefaultSettings.
 *
 * Default settings:
 * https://github.com/cequence-io/openai-scala-client/blob/master/openai-core/src/main/scala/io/cequence/openaiscala/service/OpenAIServiceConsts.scala#L17
 *
 * To see all available parameters to tweak:
 * https://github.com/cequence-io/openai-scala-client/blob/master/openai-core/src/main/scala/io/cequence/openaiscala/domain/settings/CreateChatCompletionSettings.scala#L5
 */
object CustomSettings {
  val customSetting1: CreateChatCompletionSettings =
    CreateChatCompletionSettings(
      model = ModelId.gpt_4o_mini,
      max_tokens = Some(1000)
    )

  def createCustomJsonChatCompletion(
    model: String,
    maxTokens: Int,
    jsonSchema: JsonSchemaDef
  ): CreateChatCompletionSettings =
    CreateChatCompletionSettings(
      model = model,
      max_tokens = Option(maxTokens),
      response_format_type = Some(ChatCompletionResponseFormatType.json_schema),
      jsonSchema = Some(jsonSchema)
    )
}
