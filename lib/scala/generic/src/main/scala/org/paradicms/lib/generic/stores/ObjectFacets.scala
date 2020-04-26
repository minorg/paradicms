package org.paradicms.lib.generic.stores

final case class ObjectFacets(
                               culturalContexts: List[String],
                               materials: List[String],
                               spatials: List[String],
                               subjects: List[String],
                               techniques: List[String],
                               temporals: List[String],
                               types: List[String]
                             )
