package org.paradicms.lib.generic.controllers.auth0

import java.net.URLEncoder

import io.lemonlabs.uri.Uri
import javax.inject.Inject
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.stores.UserStore
import play.api.Configuration
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.libs.ws._
import play.api.mvc.{Action, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Auth0Controller @Inject()(ws: WSClient, configuration: Configuration, userStore: UserStore) extends InjectedController {
  private val config = Auth0Configuration(configuration)

  final def login(returnTo: String): Action[AnyContent] = Action {
    val state = returnTo // "unused"

    var audience = config.audience
    if (config.audience == "") {
      audience = String.format("https://%s/userinfo", config.domain)
    }

    val query = String.format(
      "authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=openid profile&audience=%s&state=%s",
      config.clientId,
      config.callbackURL,
      audience,
      state
    )
    Redirect(String.format("https://%s/%s", config.domain, query))
  }

  final def loginCallback(codeOpt: Option[String] = None, stateOpt: Option[String]): Action[AnyContent] = Action.async { request =>
    val returnTo = stateOpt.get

    (for {
      code <- codeOpt
    } yield {
      getTokens(code).flatMap { case (_, accessToken) =>
        getUserinfo(accessToken).map { userinfo =>
          val userOpt = parseUserinfo(userinfo)
          if (userOpt.isDefined) {
            Redirect(returnTo).withSession(new CurrentUser(userStore).put(userOpt.get))
          } else {
            Redirect(returnTo)
          }
        }
      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }).getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  private def getTokens(code: String): Future[(String, String)] = {
    var audience = config.audience
    if (config.audience == "") {
      audience = String.format("https://%s/userinfo", config.domain)
    }
    val tokenResponse = ws.url(String.format("https://%s/oauth/token", config.domain)).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> config.clientId,
          "client_secret" -> config.secret,
          "redirect_uri" -> config.callbackURL,
          "code" -> code,
          "grant_type" -> "authorization_code",
          "audience" -> audience
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
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

  private def parseUserinfo(userinfo: JsValue): Option[User] = {
    if (!userinfo.isInstanceOf[JsObject]) {
      return None
    }
    val userinfoObj = userinfo.asInstanceOf[JsObject].value
    val email = userinfoObj.get("email")
    val sub = userinfoObj.get("sub")
    val name = userinfoObj.get("name")
    if (!name.isDefined || !name.get.isInstanceOf[JsString]) {
      return None
    }
    if (!sub.isDefined || !sub.get.isInstanceOf[JsString]) {
      return None
    }
    Some(User(
      email = email.flatMap(value => if (value.isInstanceOf[JsString]) Some(value.asInstanceOf[JsString].value) else None),
      name = name.get.asInstanceOf[JsString].value,
      uri = Uri.parse("http://www.paradicms.org/user/" + URLEncoder.encode(sub.get.asInstanceOf[JsString].value, "UTF-8"))
    ))
  }

  final def logout(returnTo: String): Action[AnyContent] = Action { request =>
    Redirect(String.format(
      "https://%s/v2/logout?client_id=%s&returnTo=%s",
      config.domain,
      config.clientId,
      returnTo)
    ).withNewSession
  }
}