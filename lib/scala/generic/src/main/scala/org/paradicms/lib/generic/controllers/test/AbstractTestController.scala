package org.paradicms.lib.generic.controllers.test

import controllers.Assets
import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.controllers.auth0.CurrentUser
import org.paradicms.lib.generic.models.domain.User
import org.paradicms.lib.generic.stores.UserStore
import play.api.mvc.{Action, AnyContent, InjectedController}

abstract class AbstractTestController (assets: Assets, userStore: UserStore) extends InjectedController {
  protected def checkTestEnvironment(): Boolean

  final def frontEndPath(path: String): Action[AnyContent] = {
    if (checkTestEnvironment()) {
      if (path.endsWith(".css") || path.endsWith(".html") || path.endsWith(".ico") || path.endsWith(".js")) {
        // If the path has a file extension, assume it's a file and not a React URL
        // This is simpler than more complicated code that tests if the file exists, which didn't work for both dev (public/ file system) and production (assets.jar) cases.
        assets.at("/public", path, aggressiveCaching = false)
      } else {
        assets.at("/public", "index.html", aggressiveCaching = false)
      }
    } else {
      Action {
        InternalServerError("Not in testing environment")
      }
    }
  }

  final def login(returnTo: String, userUri: Option[String]) = Action {
    if (checkTestEnvironment()) {
      val user = User(email = Some("test@example.com"), name = "Test user", uri = if (userUri.isDefined) Uri.parse(userUri.get) else Uri.parse("http://example.com/user"))
      if (!userStore.getUserByUri(user.uri).isDefined) {
        userStore.putUser(user)
      }

      Redirect(returnTo).withSession(new CurrentUser(userStore).put(user))
    } else {
      InternalServerError("Not in testing environment")
    }
  }

  final def logout(returnTo: String) = Action {
    Redirect(returnTo).withNewSession
  }

  final def reset() = Action {
    if (checkTestEnvironment()) {
      doReset()
      Redirect("/").withNewSession
    } else {
      InternalServerError("Not in testing environment")
    }
  }

  protected def doReset(): Unit
}
