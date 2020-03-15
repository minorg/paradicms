package org.paradicms.lib.base.models.domain

import org.apache.jena.vocabulary.RDFS

import scala.collection.JavaConverters._

trait RdfsResourceProperties extends ResourceProperties {
  def comments: List[String] = {
    var commentStatements = resource.listProperties(RDFS.comment, "en").asScala.toList
    if (commentStatements.isEmpty) {
      commentStatements = resource.listProperties(RDFS.comment).asScala.toList
    }
    commentStatements.map(statement => statement.getObject.asLiteral().getString)
  }
}
