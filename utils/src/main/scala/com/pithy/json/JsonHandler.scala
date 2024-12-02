package com.pithy.json

import com.pithy.errors.{AppError, FileNotFoundError, FileWriteError, JsonError, JsonParseError}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.{Logger, LoggerFactory}

import java.io.{FileNotFoundException, IOException, PrintWriter}
import scala.concurrent.Future
import scala.util.Try
import scala.util.control.NonFatal

/**
 * JSON parser, printer, handler
 */
object JsonHandler {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  // converts a JSON string to a JSON object
  def parseJson(jsonString: String): Either[JsonError, Json] = {
    logger.info(s"Start parsing JSON response: $jsonString")
    parse(jsonString) match {
      case Left(error: ParsingFailure) =>
        logger.error(s"Failed to parse JSON response. ${error.message}")
        Left(JsonParseError(s"Failed to parse JSON string"))
      case Right(json) =>
        logger.info("Successfully parsed JSON response.")
        Right(json)
    }
  }

  /**
   * saves a pretty-printed copy of the JSON response to a temporary file
   * the file will be uploaded to cloud/DB and will be overwritten on the next call of this function
   */
  def saveTempCopyAsJson(
                          json: Json,
                          filePath: String
                        ): Either[AppError, Unit] =
    Try {
      val prettyJson = json.spaces2

      // Store it in a file
      val writer = new PrintWriter(filePath)
      logger.info(s"Start writing prettyJson to $filePath...")
      writer.write(prettyJson)
      writer.close()
      logger.info(s"Successfully stored prettyJson to $filePath.")
    }.toEither.left.map{
      case e: FileNotFoundException => FileNotFoundError(s"Failed to write Json to file: File doesn't exist $filePath. Error: ${e.getMessage}")
      case e: IOException => FileWriteError(s"Failed to write Json to file: IO error. Error: ${e.getMessage}")
      case NonFatal(e) => FileWriteError(s"Failed to write Json to file: ${e.getMessage}")
    }

  // pretty print a JSON string
  def prettyPrintJson(json: Json): String = json.spaces2
}