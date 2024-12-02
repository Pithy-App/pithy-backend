package com.pithy

import com.typesafe.config.ConfigFactory

case class DbConfig(url: String, user: String, password: String, driver: String, maxPoolSize: Int)

object DbConfig {
  def load(): DbConfig = {
    val config = ConfigFactory.load().getConfig("db")
    DbConfig(
      url = config.getString("url"),
      user = config.getString("user"),
      password = config.getString("password"),
      driver = config.getString("driver"),
      maxPoolSize = config.getInt("maxPoolSize")
    )
  }
}
