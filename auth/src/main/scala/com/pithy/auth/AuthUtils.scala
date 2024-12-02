package com.pithy.auth

/**
 * Utility functions for authentication
 */
object AuthUtils {

  /**
   * Generates a random string of length 32
   */
  def generate32String: String = {
    val random = new scala.util.Random
    random.alphanumeric.take(32).mkString
  }
}
