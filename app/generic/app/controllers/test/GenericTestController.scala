package controllers.test

import java.io.File

import controllers.{Assets, AssetsFinder}
import javax.inject.{Inject, Singleton}
import play.api.Environment
import play.api.mvc.InjectedController
import stores.{GenericStore, TestGenericStore}

@Singleton
class GenericTestController @Inject()(assets: Assets, assetsFinder: AssetsFinder, environment: Environment, store: GenericStore) extends InjectedController {
  def frontEndPath(path: String) = {
    val file = new File(s"${environment.rootPath}${assetsFinder.assetsBasePath}${File.separator}${path}").getAbsoluteFile
    if (file.exists) {
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
