package controllers.graphql

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.graphql.{GraphQlSchemaContext, GraphQlSchemaDefinition}
import org.paradicms.service.lib.controllers.graphql.AbstractGraphQlController
import org.paradicms.service.lib.stores.Store
import play.api.mvc.Request

@Singleton
class GraphQlController @Inject()(store: Store, system: ActorSystem) extends AbstractGraphQlController[GraphQlSchemaContext](GraphQlSchemaDefinition.schema, system) {
  override protected def getContext(request: Request[_]): GraphQlSchemaContext = new GraphQlSchemaContext(request, store)
}
