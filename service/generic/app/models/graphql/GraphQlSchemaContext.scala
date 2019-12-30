package models.graphql

import io.lemonlabs.uri.Uri
import javax.inject.Inject
import org.paradicms.service.lib.models.domain.User
import org.paradicms.service.lib.stores.Store

class GraphQlSchemaContext @Inject()(val store: Store) {
  def currentUser(): Option[User] = None

  def currentUserUri(): Option[Uri] = currentUser().map(user => user.uri)
}
