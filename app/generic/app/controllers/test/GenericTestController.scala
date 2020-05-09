package controllers.test

import javax.inject.{Inject, Singleton}
import play.api.mvc.InjectedController
import stores.{GenericStore, TestGenericStore}

@Singleton
class GenericTestController @Inject()(store: GenericStore) extends InjectedController {
  def reset = Action { request =>
    if (request.headers.hasHeader("X-Forwarded-For")) {
      Forbidden("proxy requests not allowed")
    } else if (!store.isInstanceOf[TestGenericStore]) {
      InternalServerError("test store not enabled");
    } else {
      store.asInstanceOf[TestGenericStore].reset()
      Ok("OK")
    }
  }
}
