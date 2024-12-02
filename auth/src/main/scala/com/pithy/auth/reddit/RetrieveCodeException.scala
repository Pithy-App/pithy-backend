package com.pithy.auth.reddit

/**
 * Possible exceptions that can occur when retrieving code from user
 */
sealed trait RetrieveCodeExceptions extends Throwable {
  def message: String
}

// TODO PITH-11: Selectively show messages to user
// Show to user
case object AccessDenied extends RetrieveCodeExceptions {
  override val message: String =
    "Hi! It seems you denied access. Please try again."
}

// Do not show to user the exact messages. Only show "error occurred, please try again"
case object UnsupportedResponseType extends RetrieveCodeExceptions {
  override val message: String = "response_type parameter is not supported."
}

case object InvalidScope extends RetrieveCodeExceptions {
  override val message: String = "scope parameter is invalid."
}

case object InvalidRequest extends RetrieveCodeExceptions {
  override val message: String =
    "request is invalid. Double check the parameters being sent during the request to /api/v1/authorize. "
}
