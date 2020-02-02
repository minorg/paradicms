package controllers.graphql

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.graphql.{BookGraphQlSchemaContext, BookGraphQlSchemaDefinition}
import org.paradicms.lib.generic.controllers.graphql.AbstractGraphQlController
import org.paradicms.lib.generic.stores.GenericStore
import play.api.mvc.Request

@Singleton
class BookGraphQlController @Inject()(store: GenericStore, system: ActorSystem) extends AbstractGraphQlController[BookGraphQlSchemaContext](BookGraphQlSchemaDefinition.schema, system) {
  override protected def getContext(request: Request[_]): BookGraphQlSchemaContext = new BookGraphQlSchemaContext(request, store)
}
