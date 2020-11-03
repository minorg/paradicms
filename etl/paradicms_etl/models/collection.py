from dataclasses import dataclass
from typing import Optional, Tuple

from dataclasses_json import LetterCase, dataclass_json
from rdflib import Graph, Literal, URIRef
from rdflib.namespace import DCTERMS, RDF
from rdflib.resource import Resource

from paradicms_etl.models._named_model import _NamedModel
from paradicms_etl.models.property import Property
from paradicms_etl.models.property_definition import PropertyDefinition
from paradicms_etl.namespace import CMS


@dataclass_json(letter_case=LetterCase.CAMEL)
@dataclass(frozen=True)
class Collection(_NamedModel):
    # Linking up to the parent (relational style )instead of down to child objects
    # makes it easier to do page generation and search indexing downstream.
    institution_uri: URIRef
    title: str
    abstract: Optional[str] = None
    properties: Tuple[Property, ...] = ()

    def to_rdf(
        self, *, graph: Graph, property_definitions: Tuple[PropertyDefinition, ...]
    ) -> Resource:
        resource = _NamedModel.to_rdf(
            self, graph=graph, property_definitions=property_definitions
        )
        resource.add(RDF.type, CMS[self.__class__.__name__])
        graph.add((self.institution_uri, CMS.collection, self.uri))
        self._properties_to_rdf(
            property_definitions=property_definitions, resource=resource
        )
        resource.add(DCTERMS.title, Literal(self.title))
        return resource
