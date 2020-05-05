package org.paradicms.lib.base.rdf.properties

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

//  final def comments = getPropertyObjectStrings(RDFS.comment) ++ getPropertyObjectStrings(RDFS.comment, "en")
  final def comments_=(comments: List[String]) = setPropertyLiteral(RDFS.comment, comments)

  final def labels = getPropertyObjectStrings(RDFS.label)
  final def labels_=(labels: List[String]) = setPropertyLiteral(RDFS.label, labels)
}
