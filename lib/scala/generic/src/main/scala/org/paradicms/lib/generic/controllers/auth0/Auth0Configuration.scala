package org.paradicms.lib.generic.controllers.auth0

import play.api.Configuration

final case class Auth0Configuration(secret: String, clientId: String, callbackURL: String, domain: String, audience: String)

object Auth0Configuration {
  def apply(configuration: Configuration): Auth0Configuration = {
    Auth0Configuration(
      configuration.get[String]("auth0.clientSecret"),
      configuration.get[String]("auth0.clientId"),
          configuration.get[String]("auth0.callbackURL"),
          configuration.get[String]("auth0.domain"),
          configuration.get[String]("auth0.audience")
    )
  }
}
