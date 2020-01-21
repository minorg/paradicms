package stores

import com.google.inject.AbstractModule
import org.paradicms.service.lib.stores.{SparqlStore, Store}
import play.api.{Configuration, Environment}

class StoreModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Store]).toInstance(new SparqlStore(configuration))
  }
}
