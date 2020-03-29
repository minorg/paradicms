package stores

import com.google.inject.AbstractModule
import org.paradicms.lib.generic.stores.UserStore
import stores.sparql.FusekiGenericStore

final class StoresModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GenericStore]).to(classOf[FusekiGenericStore])
    bind(classOf[UserStore]).to(classOf[FusekiGenericStore])
  }
}
