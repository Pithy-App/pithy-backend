package com.pithy

import com.amazonaws.services.lambda.runtime.{
  Context,
  LambdaLogger,
  RequestHandler
}
import com.fasterxml.jackson.databind.ObjectMapper
import com.pithy.PreflightHandler.handlePreflightRequest
import com.pithy.shared.LambdaEvents

import java.util
import scala.jdk.CollectionConverters._

/**
 * A simple AWS Lambda handler that performs tasks based on user input from frontend/aws Test Event/general POST request.
 */
class ProcessUserInputHandler
    extends RequestHandler[util.Map[String, Any], String] {

  private val mapper: ObjectMapper = new ObjectMapper()

  override def handleRequest(
    event: util.Map[String, Any],
    context: Context
  ): String = {

    val logger: LambdaLogger = context.getLogger
    logger.log("Received event: " + event)

    // handle preflight request
    handlePreflightRequest(event, context) match {
      case Some(response) => return response
      case None           => ()
    }

    val scalaEvent = event.asScala.getOrElse("body", event) match {
      case body: String =>
        logger.log("Received API Gateway event")
        mapper.readValue(body, classOf[LambdaEvents])
      case body: util.Map[_, _] =>
        logger.log("Received AWS test event")
        mapper.convertValue(body, classOf[LambdaEvents])
      case _ =>
        throw new IllegalArgumentException("Invalid event")
    }

    // Which platform?
    scalaEvent.getPlatform match {
      case "reddit" =>
        Launch.launchReddit(scalaEvent.getPostUrl, scalaEvent.getQueries)
    }
  }
}
