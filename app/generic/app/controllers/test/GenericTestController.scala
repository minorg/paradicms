package controllers.test

import controllers.Assets
import javax.inject.{Inject, Singleton}
import org.paradicms.lib.generic.controllers.test.AbstractTestController
import stores.{GenericStore, TestGenericStore}

@Singleton
class GenericTestController @Inject()(assets: Assets, store: GenericStore) extends AbstractTestController(assets, store) {
  override protected def checkTestEnvironment = store.isInstanceOf[TestGenericStore]
  override protected def doReset() = store.asInstanceOf[TestGenericStore].reset()
}
