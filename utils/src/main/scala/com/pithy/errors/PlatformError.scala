package com.pithy.errors

/**
 * Very general errors that can happen in the application
 */
sealed trait AppError

/**
 * Errors specific to http requests
 */
sealed trait RequestError extends AppError

case class TimeoutError(message: String) extends RequestError
case class UnmarshalError(message: String) extends RequestError
case class HttpError(statusCode: Int, message: String) extends RequestError

case class GeneralError(message: String) extends RequestError

/**
 * Errors specific to JSON processing
 */
sealed trait JsonError extends AppError
case class JsonParseError(message: String) extends JsonError

case class FileNotFoundError(message: String) extends AppError
case class FileWriteError(message: String) extends AppError

/**
 * Errors shared by all aspects of the application
 */
case class UnknownError(message: String, cause: Throwable) extends AppError

/**
 * Reddit Errors
 */
sealed trait RedditError extends AppError
case class RedditResponseDecodeError(message: String) extends RedditError





