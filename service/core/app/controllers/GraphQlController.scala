package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import models.graphql.{GraphQlSchemaContext, GraphQlSchemaDefinition}
import org.paradicms.service.lib.controllers.AbstractGraphQlController

@Singleton
class GraphQlController @Inject()(context: GraphQlSchemaContext, system: ActorSystem) extends AbstractGraphQlController[GraphQlSchemaContext](context, GraphQlSchemaDefinition.schema, system) {
}
