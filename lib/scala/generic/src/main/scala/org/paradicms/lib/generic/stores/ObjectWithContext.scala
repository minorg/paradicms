package org.paradicms.lib.generic.stores

import io.lemonlabs.uri.Uri
import org.paradicms.lib.generic.models.domain.Object

final case class ObjectWithContext(
                                    collectionUri: Uri,
                                    institutionUri: Uri,
                                    object_ : Object
                                  )