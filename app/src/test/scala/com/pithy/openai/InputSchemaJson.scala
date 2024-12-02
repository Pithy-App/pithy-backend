package com.pithy.openai

import io.cequence.openaiscala.domain.JsonSchema
import io.cequence.openaiscala.domain.settings.JsonSchemaDef
import org.slf4j.{Logger, LoggerFactory}

trait InputSchemaJson {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val prompt = "Tell me whether each comment agrees with the post or does not agree with the post in JSON format."

  val capitalsSchemaDef1: JsonSchemaDef = capitalsSchemaDefAux(Left(capitalsSchema1))

  val capitalsSchemaDef2: JsonSchemaDef = capitalsSchemaDefAux(Right(capitalsSchema2))

  def capitalsSchemaDefAux(schema: Either[JsonSchema, Map[String, Any]]): JsonSchemaDef =
    JsonSchemaDef(
      name = "capitals_response",
      strict = true,
      structure = schema
    )

  // TODO: This example schema raises an error, communicating with the maintainer right now
  lazy protected val capitalsSchema1: JsonSchema.Object = JsonSchema.Object(
    properties = Map(
      "countries" -> JsonSchema.Array(
        items = JsonSchema.Object(
          properties = Map(
            "country" -> JsonSchema.String(
              description = Some("The name of the country")
            ),
            "capital" -> JsonSchema.String(
              description = Some("The capital city of the country")
            )
          ),
          required = Seq("country", "capital")
        )
      )
    ),
    required = Seq("countries")
  )

  lazy protected val capitalsSchema2: Map[String, Object] = Map(
    "type" -> "object",
    "properties" -> Map(
      "countries" -> Map(
        "type" -> "array",
        "items" -> Map(
          "type" -> "object",
          "properties" -> Map(
            "country" -> Map(
              "type" -> "string",
              "description" -> "The name of the country"
            ),
            "capital" -> Map(
              "type" -> "string",
              "description" -> "The capital city of the country"
            )
          ),
          "required" -> Seq("country", "capital")
        )
      )
    ),
    "required" -> Seq("countries")
  )

  val exampleSchemaDef2: JsonSchemaDef = capitalsSchemaDefAux(Right(exampleSchema2))
  def exampleSchemaDefAux(schema: Either[JsonSchema, Map[String, Any]]): JsonSchemaDef =
    JsonSchemaDef(
      name = "example_response",
      strict = true,
      structure = schema
    )

  lazy protected val exampleSchema2: Map[String, Object] = Map(
    "type" -> "object",
    "properties" -> Map(
      "statistics" -> Map(
        "type" -> "array",
        "items" -> Map(
          "type" -> "object",
          "properties" -> Map(
            "comment" -> Map(
              "type" -> "string",
              "description" -> "The number id of the comment"
            ),
            "status" -> Map(
              "type" -> "string",
              "enum" -> Seq("agree", "disagree", "inconclusive", "irrelevant"),
              "description" -> "The agreement status of the comment. An inconclusive comment is on topic comment that does not explicit agree or disagree. Irrelevant comments are unrelated to the topic."
            )
          ),
          "required" -> Seq("comment", "status")
        )
      )
    ),
    "required" -> Seq("statistics")
  )
}
