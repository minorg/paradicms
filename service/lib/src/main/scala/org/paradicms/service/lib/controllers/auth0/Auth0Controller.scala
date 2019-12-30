package org.paradicms.service.lib.controllers.auth0

import java.util.UUID.randomUUID

import javax.inject.Inject
import play.api.Configuration
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws._
import play.api.mvc.{Action, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Auth0Controller @Inject() (ws: WSClient, configuration: Configuration) extends InjectedController {
  private val config = Auth0Configuration(configuration)

  private def getToken(code: String, sessionId: String): Future[(String, String)] = {
    var audience = config.audience
    if (config.audience == ""){
      audience = String.format("https://%s/userinfo",config.domain)
    }
    val tokenResponse = ws.url(String.format("https://%s/oauth/token", config.domain)).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> config.clientId,
          "client_secret" -> config.secret,
          "redirect_uri" -> config.callbackURL,
          "code" -> code,
          "grant_type"-> "authorization_code",
          "audience" -> audience
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
//        cache.set(sessionId + "id_token", idToken)
//        cache.set(sessionId + "access_token", accessToken)
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent")))
    }

  }

  private def getUserinfo(accessToken: String): Future[JsValue] = {
    val userResponse = ws.url(String.format("https://%s/userinfo", config.domain))
      .withQueryString("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }

  final def login: Action[AnyContent] = Action {
    // Generate random state parameter
//    object RandomUtil {
//      private val random = new SecureRandom()
//
//      def alphanumeric(nrChars: Int = 24): String = {
//        new BigInteger(nrChars * 5, random).toString(32)
//      }
//    }
//    val state = RandomUtil.alphanumeric()
    val state = "unused"

    var audience = config.audience
    if (config.audience == ""){
      audience = String.format("https://%s/userinfo", config.domain)
    }

    val userUUID = randomUUID().toString
//    cache.set(id + "state", state)
    val query = String.format(
      "authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile&audience=%s&state=%s",
      config.clientId,
      config.callbackURL,
      audience,
      state
    )
    Redirect(String.format("https://%s/%s", config.domain, query)).withSession("id" -> userUUID)
  }

  final def loginCallback(codeOpt: Option[String] = None, stateOpt: Option[String] = None): Action[AnyContent] = Action.async { request =>
    val sessionId = request.session.get("id").get
    //    if (stateOpt == cache.get(sessionId + "state")) {
    (for {
      code <- codeOpt
    } yield {
      getToken(code, sessionId).flatMap { case (idToken, accessToken) =>
        getUserinfo(accessToken).map { user =>
          //            cache.set(request.session.get("id").get + "profile", user)
          Redirect("/loginCallback")
        }
      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
    //    } else {
    //      Future.successful(BadRequest("Invalid state parameter"))
    //    }
  }

  final def logout: Action[AnyContent] = Action { request =>
    val host = request.host
    var scheme = "http"
    if (request.secure) {
      scheme = "https"
    }
    val returnTo = scheme + "://" + host
    Redirect(String.format(
      "https://%s/v2/logout?client_id=%s&returnTo=%s",
      config.domain,
      config.clientId,
      returnTo)
    ).withNewSession
  }
}