package controllers.test

import java.io.File

import controllers.{Assets, AssetsFinder}
import javax.inject.{Inject, Singleton}
import play.api.mvc.InjectedController
import play.api.{Environment, Mode}
import stores.{GenericStore, TestGenericStore}

@Singleton
class GenericTestController @Inject()(assets: Assets, assetsFinder: AssetsFinder, environment: Environment, store: GenericStore) extends InjectedController {
  def frontEndPath(path: String) = {
    val pathExists =
      if (environment.mode == Mode.Prod) {
        val streamPath = "/public" + path
        val stream = getClass.getClassLoader.getResourceAsStream(streamPath)
        if (stream != null) {
          stream.close()
          true
        } else {
          false
        }
      } else {
        val file = new File(s"${environment.rootPath}${assetsFinder.assetsBasePath}${File.separator}${path}").getAbsoluteFile
        file.exists
      }

    if (pathExists) {
      assets.at("/public", path, aggressiveCaching = false)
    } else {
      assets.at("/public", "index.html", aggressiveCaching = false)
    }
  }

  def reset = Action { request =>
    if (!store.isInstanceOf[TestGenericStore]) {
      InternalServerError("test store not enabled");
    } else {
      store.asInstanceOf[TestGenericStore].reset()
      Ok("OK")
    }
  }
}
