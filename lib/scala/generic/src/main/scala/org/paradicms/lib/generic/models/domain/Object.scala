package org.paradicms.lib.generic.models.domain

import io.lemonlabs.uri.Uri

final case class Object(
                         alternativeTitles: List[String] = List(),
                         creators: List[String] = List(),
                         culturalContexts: List[String] = List(),
                         dates: List[String] = List(),
                         description: Option[String] = None,
                         descriptions: List[String] = List(),
                         extents: List[String] = List(),
                         identifiers: List[String] = List(),
                         images: List[DerivedImageSet] = List(),
                         languages: List[String] = List(),
                         materials: List[String] = List(),
                         media: List[String] = List(),
                         provenances: List[String] = List(),
                         publishers: List[String] = List(),
                         rights: Option[Rights] = None,
                         sources: List[String] = List(),
                         spatials: List[String] = List(),
                         subjects: List[String] = List(),
                         techniques: List[String] = List(),
                         temporals: List[String] = List(),
                         title: String,
                         titles: List[String],
                         types: List[String],
                         uri: Uri
                       )
