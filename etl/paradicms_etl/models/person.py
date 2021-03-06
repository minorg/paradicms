from dataclasses import dataclass
from typing import Optional

from rdflib import Graph, Literal
from rdflib.namespace import FOAF, RDF
from rdflib.resource import Resource

from paradicms_etl.models._named_model import _NamedModel
from paradicms_etl.namespace import CMS, CONTACT
from paradicms_etl.utils.rdf_resource_wrapper import RdfResourceWrapper


@dataclass(frozen=True)
class Person(_NamedModel):
    name: str
    family_name: Optional[str] = None
    given_name: Optional[str] = None
    sort_name: Optional[str] = None

    def to_rdf(self, *, graph: Graph) -> Resource:
        resource = _NamedModel.to_rdf(self, graph=graph)
        if self.family_name is not None:
            resource.add(FOAF.familyName, Literal(self.family_name))
        if self.given_name is not None:
            resource.add(FOAF.givenName, Literal(self.given_name))
        resource.add(FOAF.name, Literal(self.name))
        if self.sort_name is not None:
            resource.add(CONTACT.sortName, Literal(self.sort_name))
        return resource
