from dataclasses import dataclass, field
from datetime import datetime
from typing import List, Optional

from dataclasses_json import LetterCase, dataclass_json
from rdflib import Graph, Literal, URIRef
from rdflib.namespace import DCTERMS, FOAF, RDF
from rdflib.resource import Resource

from paradicms_etl._model import _Model
from paradicms_etl.models.image_dimensions import ImageDimensions
from paradicms_etl.models.rights import Rights
from paradicms_etl.namespace import CMS, EXIF


@dataclass_json(letter_case=LetterCase.CAMEL)
@dataclass(frozen=True)
class _Image(_Model):
    """
    Base class for image models that can be reused by other systems.
    """

    created: Optional[datetime]
    exact_dimensions: Optional[ImageDimensions]
    format: Optional[str]
    max_dimensions: Optional[ImageDimensions]
    modified: Optional[datetime]
    original_image_uri: Optional[URIRef]

    def to_rdf(self, *, graph: Graph, **kwds) -> Resource:
        resource = _Model.to_rdf(self, graph=graph, **kwds)
        if self.created is not None:
            resource.add(DCTERMS.created, Literal(self.created))
        if self.format is not None:
            resource.add(DCTERMS["format"], Literal(self.format))
        if self.exact_dimensions is not None:
            resource.add(EXIF.height, Literal(self.exact_dimensions.height))
            resource.add(EXIF.width, Literal(self.exact_dimensions.width))
        elif self.max_dimensions is not None:
            resource.add(CMS.imageMaxHeight, Literal(self.max_dimensions.height))
            resource.add(CMS.imageMaxWidth, Literal(self.max_dimensions.width))
        if self.modified is not None:
            resource.add(DCTERMS.modified, Literal(self.modified))
        if self.original_image_uri is not None:
            graph.add((self.original_image_uri, FOAF.thumbnail, self.uri))
        return resource
