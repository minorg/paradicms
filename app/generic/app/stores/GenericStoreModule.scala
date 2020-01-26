package stores

import com.google.inject.AbstractModule
import org.paradicms.service.lib.generic.stores.{SparqlStore, Store}
import play.api.{Configuration, Environment}

class GenericStoreModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Store]).toInstance(new SparqlStore(configuration))
  }
}
