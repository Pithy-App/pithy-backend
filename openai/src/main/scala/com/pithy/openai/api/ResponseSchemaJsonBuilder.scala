package com.pithy.openai.api

import com.pithy.PlatformMainInput
import com.pithy.openai.OpenAIMainInput
import io.cequence.openaiscala.domain.JsonSchema
import io.cequence.openaiscala.domain.settings.JsonSchemaDef
//import org.slf4j.{Logger, LoggerFactory}

/**
 * Represents the JSON schema we instruct the OpenAI API to use for the response.
 */
object ResponseSchemaJsonBuilder {

  // private method that generates a JsonSchemaDef object from either a safe JsonSchema or a dynamic Map[String, Any]
  private def responseSchemaDefAux(
    schema: Either[JsonSchema, Map[String, Any]]
  ): JsonSchemaDef =
    JsonSchemaDef(
      name = "response",
      strict = true,
      structure = schema
    )

  // Generates a JsonSchemaDef object from the input to the `platform` module
  def generateResponseSchemaJsonFromPlatform(
    userInput: OpenAIMainInput
  ): JsonSchemaDef = {

    val queryKeys = userInput.queries.keys.toSeq

    val queryKeyDescriptions = userInput.queries.map { case (key, query) =>
      s"\n\"$key\" means $query"
    }.mkString

    val responseSchemaSafe = JsonSchema.Object(
      properties = Map(
        "statistics" -> JsonSchema.Array(
          items = JsonSchema.Object(
            properties = Map(
              "commentId" -> JsonSchema.Number(
                description = Some("The number id of the comment")
              ),
              "queryKey" -> JsonSchema.String(
                description = Some(queryKeyDescriptions),
                enum = queryKeys
              )
            ),
            required = Seq("commentId", "queryKey")
          )
        )
      ),
      required = Seq("statistics")
    )

    val responseSchemaDynamic = Map(
      "type" -> "object",
      "properties" -> Map(
        "statistics" -> Map(
          "type" -> "array",
          "items" -> Map(
            "type" -> "object",
            "properties" -> Map(
              "commentId" -> Map(
                "type" -> "number",
                "description" -> "The number (int) id of the comment"
              ),
              "queryKey" -> Map(
                "type" -> "string",
                "enum" -> queryKeys,
                "description" -> queryKeyDescriptions
              )
            ),
            "required" -> Seq("commentId", "queryKey")
          )
        )
      ),
      "required" -> Seq("statistics")
    )

    responseSchemaDefAux(
      Right(responseSchemaDynamic)
    )
  }

  val ResponseSchemaDef1: JsonSchemaDef = responseSchemaDefAux(
    Left(responseSchemaSafe)
  )

  val ResponseSchemaDef2: JsonSchemaDef = responseSchemaDefAux(
    Right(responseSchemaDynamic)
  )

  val ResponseSchemaDefCustomInput: JsonSchemaDef = responseSchemaDefAux(
    Right(responseSchemaDynamicCustomInput)
  )

  // Schema Option 1: Typesafe JSON schema
  // Use this if response needs very strict schema validation OR we have gotten bad results from Option 2
  private lazy val responseSchemaSafe = JsonSchema.Object(
    properties = Map(
      "statistics" -> JsonSchema.Array(
        items = JsonSchema.Object(
          properties = Map(
            "comment" -> JsonSchema.String(
              description = Some("The number id of the comment")
            ),
            "queryKey" -> JsonSchema.String(
              description = Some("The agreement queryKey of the comment"),
              enum = Seq("agree", "disagree", "neutral")
            )
          ),
          required = Seq("comment", "queryKey")
        )
      )
    ),
    required = Seq("statistics")
  )

  // Schema Option 2: Dynamic JSON schema
  private lazy val responseSchemaDynamic = Map(
    "type" -> "object",
    "properties" -> Map(
      "statistics" -> Map(
        "type" -> "array",
        "items" -> Map(
          "type" -> "object",
          "properties" -> Map(
            "commentId" -> Map(
              "type" -> "number",
              "description" -> "The number (int) id of the comment"
            ),
            "queryKey" -> Map(
              "type" -> "string",
              "enum" -> Seq("agree", "disagree", "inconclusive"),
              "description" -> "The attitude of the comment. 'agree' means the comment agrees with the post, 'disagree' means the comment disagrees with the post, 'inconclusive' means the comment is either neutral or irrelevant or the attitude is not clear."
            )
          ),
          "required" -> Seq("commentId", "queryKey")
        )
      )
    ),
    "required" -> Seq("statistics")
  )

  // Schema Option 2: Dynamic JSON schema
  private lazy val responseSchemaDynamicCustomInput = Map(
    "type" -> "object",
    "properties" -> Map(
      "statistics" -> Map(
        "type" -> "array",
        "items" -> Map(
          "type" -> "object",
          "properties" -> Map(
            "commentId" -> Map(
              "type" -> "number",
              "description" -> "The number (int) id of the comment"
            ),
            "queryKey" -> Map(
              "type" -> "string",
              "enum" -> Seq("helpful", "unhelpful", "inconclusive"),
              "description" -> "The queryKey of the comment. 'helpful' means the comment gives sincere useful information regarding the post, 'unhelpful' means the comment does not have any meaningful outputs or trolls, 'inconclusive' means the comment is trying to give information but has no final conclusion."
            )
          ),
          "required" -> Seq("commentId", "queryKey")
        )
      )
    ),
    "required" -> Seq("statistics")
  )
}
