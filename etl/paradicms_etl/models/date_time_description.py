from dataclasses import dataclass
from typing import Optional

from dataclasses_json import LetterCase, dataclass_json
from rdflib import Graph, Literal, RDF, XSD
from rdflib.resource import Resource

from paradicms_etl._model import _Model
from paradicms_etl.namespace import TIME


@dataclass_json(letter_case=LetterCase.CAMEL)
@dataclass(frozen=True)
class DateTimeDescription(_Model):
    day: Optional[int] = None
    hour: Optional[int] = None
    minute: Optional[int] = None
    second: Optional[int] = None
    month: Optional[int] = None
    year: Optional[int] = None

    def to_rdf(self, *, graph: Graph, **kwds) -> Resource:
        resource = _Model.to_rdf(self, graph=graph)
        resource.add(RDF.type, TIME.DateTimeDescription)
        if self.day is not None:
            resource.add(TIME.day, Literal("---" + str(self.day), datatype=XSD.gDay))
        if self.hour is not None:
            resource.add(TIME.hour, Literal(self.hour))
        if self.minute is not None:
            resource.add(TIME.minute, Literal(self.minute))
        if self.month is not None:
            resource.add(
                TIME.month, Literal("---" + str(self.month), datatype=XSD.gMonth)
            )
        if self.year is not None:
            resource.add(TIME.year, Literal(str(self.year), datatype=XSD.gYear))
        return resource
