package com.pithy

import io.cequence.openaiscala.domain.BaseMessage

/**
 * PlatformResponseBuilder is a trait that defines the interface for building output responses of different platforms' apis.
 */
trait PlatformResponseBuilder {
  def toOpenAiMessage: Either[Throwable, Seq[BaseMessage]]
}
