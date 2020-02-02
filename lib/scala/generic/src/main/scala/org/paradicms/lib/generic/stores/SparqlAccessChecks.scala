package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri

trait SparqlAccessChecks extends SparqlConnectionLoanPatterns {
  /**
   * Create ready-to-use WHERE block contents that wrap common query-specific patterns in access check UNION blocks
   */
  protected final def accessCheckGraphPatterns(collectionVariable: Option[String], currentUserUri: Option[Uri], institutionVariable: String, objectVariable: Option[String], queryPatterns: List[String]): String = {
    var unionPatterns: List[List[String]] = institutionAccessCheckGraphPatterns(currentUserUri = currentUserUri, institutionVariable = institutionVariable, unionPatterns = List(queryPatterns))
    if (collectionVariable.isDefined) {
      unionPatterns = collectionAccessCheckGraphPatterns(collectionVariable = collectionVariable.get, currentUserUri = currentUserUri, unionPatterns = unionPatterns)
    }
    if (objectVariable.isDefined) {
      unionPatterns = objectAccessCheckGraphPatterns(currentUserUri = currentUserUri, objectVariable = objectVariable.get, unionPatterns = unionPatterns)
    }
    unionPatterns.map(unionPattern => s"{ ${unionPattern.mkString("\n")} }").mkString("\nUNION\n")
  }

  /**
   * See institutionAccessChecks description.
   */
  private def collectionAccessCheckGraphPatterns(collectionVariable: String, currentUserUri: Option[Uri], unionPatterns: List[List[String]]): List[List[String]] =
    inheritableAccessCheckGraphPatterns(currentUserUri = currentUserUri, modelVariable = collectionVariable, unionPatterns = unionPatterns)

  /**
   * Given a list of union patterns (a list of lists of graph patterns), add permutations for an institution access check and return a new list of union patterns
   */
  private def institutionAccessCheckGraphPatterns(currentUserUri: Option[Uri], institutionVariable: String, unionPatterns: List[List[String]]): List[List[String]] = {
    val public = s"$institutionVariable cms:owner cms:public ."
    if (currentUserUri.isDefined) {
      val private_ = s"$institutionVariable cms:owner <${currentUserUri.get}> ."
      List(public, private_).flatMap(accessCheckPattern => unionPatterns.map(unionPattern => accessCheckPattern +: unionPattern))
    } else {
      unionPatterns.map(unionPattern => public +: unionPattern)
    }
  }

  private def objectAccessCheckGraphPatterns(currentUserUri: Option[Uri], objectVariable: String, unionPatterns: List[List[String]]): List[List[String]] =
    inheritableAccessCheckGraphPatterns(currentUserUri = currentUserUri, modelVariable = objectVariable, unionPatterns = unionPatterns)

  private def inheritableAccessCheckGraphPatterns(currentUserUri: Option[Uri], modelVariable: String, unionPatterns: List[List[String]]): List[List[String]] = {
    val public = s"$modelVariable cms:owner cms:inherit ."
    if (currentUserUri.isDefined) {
      val private_ = s"$modelVariable cms:owner <${currentUserUri.get}> ."
      List(public, private_).flatMap(accessCheckPattern => unionPatterns.map(unionPattern => accessCheckPattern +: unionPattern))
    } else {
      unionPatterns.map(unionPattern => public +: unionPattern)
    }
  }
}
