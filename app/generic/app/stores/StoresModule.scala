package stores

import com.google.inject.AbstractModule
import org.paradicms.lib.generic.stores.UserStore

final class StoresModule extends AbstractModule {
  override def configure(): Unit = {
    val storeClass =
      if (System.getProperty("test") != null)
        classOf[TestGenericStore]
      else
        classOf[FusekiGenericStore]
    bind(classOf[GenericStore]).to(storeClass)
    bind(classOf[UserStore]).to(storeClass)
  }
}
