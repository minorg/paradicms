package stores

import com.google.inject.AbstractModule
import io.lemonlabs.uri.Url
import org.paradicms.service.lib.stores.{SparqlStore, Store}
import play.api.{Configuration, Environment}

class StoreModule(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Store]).toInstance(new SparqlStore(Url.parse(configuration.get[String]("sparqlEndpointUrl"))))
  }
}
