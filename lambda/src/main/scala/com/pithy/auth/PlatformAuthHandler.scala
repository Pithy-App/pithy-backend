package com.pithy.auth

import com.amazonaws.services.lambda.runtime.{
  Context,
  LambdaLogger,
  RequestHandler
}
import com.fasterxml.jackson.databind.ObjectMapper
import com.pithy.shared.LambdaEvents
import com.pithy.PreflightHandler.handlePreflightRequest
import scala.jdk.CollectionConverters._

import java.util

/**
 * Lambda handler for platform authentication
 */
class PlatformAuthHandler
    extends RequestHandler[util.Map[String, Any], String] {

  private val mapper = new ObjectMapper()
  override def handleRequest(
    event: util.Map[String, Any],
    context: Context
  ): String = {
    val logger: LambdaLogger = context.getLogger
    logger.log("Received Platform Authentication Event: " + event)

    handlePreflightRequest(event, context) match {
      case Some(response) => return response
      case None           => logger.log("Not preflight request")
    }

    // Determine if it's from API Gateway or a test event
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

    AuthMain.main_v2(scalaEvent.getPlatform, scalaEvent) match {
      case Right(_) => "Success"
      case Left(_)  => "Authentication failed"
    }
  }
}
