package controllers.graphql

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.graphql.{GenericGraphQlSchemaContext, GenericGraphQlSchemaDefinition}
import org.paradicms.lib.generic.controllers.graphql.AbstractGraphQlController
import play.api.mvc.Request
import stores.GenericStore

@Singleton
class GenericGraphQlController @Inject()(store: GenericStore, system: ActorSystem) extends AbstractGraphQlController[GenericGraphQlSchemaContext](GenericGraphQlSchemaDefinition.schema, system) {
  override protected def getContext(request: Request[_]): GenericGraphQlSchemaContext = new GenericGraphQlSchemaContext(request, store)
}
