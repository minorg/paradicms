package org.paradicms.lib.generic.stores

final case class ObjectFacets(
                               spatials: Set[String],
                               subjects: Set[String],
                               temporals: Set[String],
                               types: Set[String]
                             )
