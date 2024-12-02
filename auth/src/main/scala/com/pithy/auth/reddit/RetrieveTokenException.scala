package com.pithy.auth.reddit

/**
 * Possible exceptions that can occur when retrieving access token from Reddit using code
 */
sealed trait RetrieveTokenExceptions extends Throwable {
  def message: String
  override def getMessage: String = message
}

case object FourZeroOneResponse extends RetrieveTokenExceptions {
  val message: String =
    "Client credentials sent as HTTP Basic Authorization were invalid."
}

case object UnsupportedGrantType extends RetrieveTokenExceptions {
  val message: String =
    """
      |`grant_type` parameter was invalid or Http Content type was not set correctly.
      |Verify that the `grant_type` sent is supported and make sure the content type of the http message is set to `application/x-www-form-urlencoded`.
      |""".stripMargin
}

case object MissingCode extends RetrieveTokenExceptions {
  val message: String = "`code` parameter is missing."
}

case object InvalidGrant extends RetrieveTokenExceptions {
  val message: String =
    "`code` has expired or already been used."
}

case object NoResponse extends RetrieveTokenExceptions {
  val message: String =
    "No response received from Reddit when exchanging code for token."
}
