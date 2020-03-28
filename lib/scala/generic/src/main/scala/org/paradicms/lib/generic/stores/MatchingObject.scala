package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Object

final case class MatchingObject(
                                 collectionUri: Uri,
                                 institutionUri: Uri,
                                 object_ : Object
                               )