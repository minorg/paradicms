package org.paradicms.lib.base.stores.sparql

import io.lemonlabs.uri.Uri

trait SparqlAccessCheckGraphPatterns {
  /**
   * Create ready-to-use WHERE block contents that wrap common query-specific patterns in access check UNION blocks
   */
  protected final def accessCheck(collectionVariable: Option[String], currentUserUri: Option[Uri], institutionVariable: String, objectVariable: Option[String], queryPatterns: List[String]): String = {
    var unionPatterns: List[List[String]] = institutionAccessCheck(currentUserUri = currentUserUri, institutionVariable = institutionVariable, unionPatterns = List(queryPatterns))
    if (collectionVariable.isDefined) {
      unionPatterns = collectionAccessCheck(collectionVariable = collectionVariable.get, currentUserUri = currentUserUri, unionPatterns = unionPatterns)
    }
    if (objectVariable.isDefined) {
      unionPatterns = objectAccessCheck(currentUserUri = currentUserUri, objectVariable = objectVariable.get, unionPatterns = unionPatterns)
    }
    unionPatterns.map(unionPattern => s"{ ${unionPattern.mkString("\n")} }").mkString("\nUNION\n")
  }

  /**
   * See institutionAccessChecks description.
   */
  private def collectionAccessCheck(collectionVariable: String, currentUserUri: Option[Uri], unionPatterns: List[List[String]]): List[List[String]] =
    inheritableAccessCheckGraphPatterns(currentUserUri = currentUserUri, modelVariable = collectionVariable, unionPatterns = unionPatterns)

  /**
   * Given a list of union patterns (a list of lists of graph patterns), add permutations for an institution access check and return a new list of union patterns
   */
  private def institutionAccessCheck(currentUserUri: Option[Uri], institutionVariable: String, unionPatterns: List[List[String]]): List[List[String]] = {
    val public = s"$institutionVariable cms:owner cms:public ."
    if (currentUserUri.isDefined) {
      val private_ = s"$institutionVariable cms:owner <${currentUserUri.get}> ."
      List(public, private_).flatMap(accessCheckPattern => unionPatterns.map(unionPattern => accessCheckPattern +: unionPattern))
    } else {
      unionPatterns.map(unionPattern => public +: unionPattern)
    }
  }

  private def objectAccessCheck(currentUserUri: Option[Uri], objectVariable: String, unionPatterns: List[List[String]]): List[List[String]] =
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
