package org.paradicms.service.lib.controllers.graphql

import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.renderer.SchemaRenderer
import sangria.schema.Schema
import sangria.slowlog.SlowLog

import scala.concurrent.Future
import scala.util.{Failure, Success}

abstract class AbstractGraphQlController[ContextT](schema: Schema[ContextT, Unit], system: ActorSystem) extends InjectedController {

  import system.dispatcher

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error@TooComplexQueryError) ⇒ HandledException(error.getMessage)
    case (_, error@MaxQueryDepthReachedError(_)) ⇒ HandledException(error.getMessage)
  }

  final def graphql(query: String, variables: Option[String], operation: Option[String]) = Action.async { request ⇒
    executeQuery(query, variables map parseVariables, operation, request)
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String], request: Request[_]) =
    QueryParser.parse(query) match {
      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        Executor.execute(schema, queryAst, getContext(request),
          operationName = operation,
          variables = variables getOrElse Json.obj(),
          //          deferredResolver = DeferredResolver.fetchers(SangriaPlaygroundSchemaDefinition.characters),
          exceptionHandler = exceptionHandler,
          queryReducers = List(
            QueryReducer.rejectMaxDepth[ContextT](15),
            QueryReducer.rejectComplexQueries[ContextT](4000, (_, _) ⇒ TooComplexQueryError)),
          middleware = if (request.headers.get("X-Apollo-Tracing").isDefined) SlowLog.apolloTracing :: Nil else Nil)
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
            case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
          }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) ⇒
        Future.successful(BadRequest(Json.obj(
          "syntaxError" → error.getMessage,
          "locations" → Json.arr(Json.obj(
            "line" → error.originalError.position.line,
            "column" → error.originalError.position.column)))))

      case Failure(error) ⇒
        throw error
    }

  final def graphqlBody = Action.async(parse.json) { request ⇒
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) ⇒ Some(parseVariables(vars))
      case obj: JsObject ⇒ Some(obj)
      case _ ⇒ None
    }

    executeQuery(query, variables, operation, request)
  }

  protected def getContext(request: Request[_]): ContextT;

  final def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(schema))
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")

}
