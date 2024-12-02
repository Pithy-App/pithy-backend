package com.pithy

import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}

import java.util

/**
 * Handles preflight requests from frontend
 */
object PreflightHandler {
  def handlePreflightRequest(
    event: util.Map[String, Any],
    context: Context
  ): Option[String] = {

    val logger: LambdaLogger = context.getLogger

    val httpMethod = Option(event.get("requestContext"))
      .flatMap(ctx =>
        Option(ctx.asInstanceOf[util.Map[String, Any]].get("http"))
      )
      .flatMap(http =>
        Option(http.asInstanceOf[util.Map[String, Any]].get("method"))
      )
      .map(_.asInstanceOf[String])

    // If it's a preflight request, return response with appropriate CORS headers
    if (httpMethod.contains("OPTIONS")) {
      logger.log(s"Received preflight request from ${event.get("origin")}")
      // Return response for preflight request with appropriate CORS headers
      Some(s"""
        {
          "statusCode": 200,
          "headers": {
            "Access-Control-Allow-Origin": "${event.get("origin")}",
            "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
            "Access-Control-Allow-Headers": "Content-Type, Authorization",
            "Access-Control-Allow-Credentials": "true"
          },
          "body": ""
        }
      """)
    } else None
  }
}
