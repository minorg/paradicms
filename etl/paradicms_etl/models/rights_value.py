from dataclasses import dataclass
from typing import Optional, Tuple, Union

from rdflib import Literal, URIRef
from rdflib.resource import Resource

from paradicms_etl.models.property import Property


@dataclass(init=False)
class RightsValue:
    """
    The rights properties such as dcterms:rights and dcterms:rightsHolder can have matching URI and text values (objects).
    For display purposes it's useful to keep the matching values in the same place.
    """

    text: Optional[str] = None
    uri: Optional[URIRef] = None

    def __init__(
        self, text: Optional[str] = None, uri: Union[None, str, URIRef] = None
    ):
        assert text or uri

        if text is not None and not isinstance(text, str):
            raise TypeError(type(text))
        self.text = text

        if uri is not None:
            if isinstance(uri, URIRef):
                pass
            elif isinstance(uri, str):
                uri = URIRef(uri)
            else:
                raise TypeError(type(uri))
        self.uri = uri

    @classmethod
    def from_properties(cls, properties: Tuple[Property, ...]):
        if not properties:
            return None

        property_definition_uri = None
        text_property_values = []
        uri_property_values = []
        for property_ in properties:
            if property_definition_uri is None:
                property_definition_uri = property_.uri
            elif property_.uri != property_definition_uri:
                raise ValueError(
                    f"rights properties with different URIs: {property_.uri} vs. {property_definition_uri}"
                )

            if isinstance(property_.value, Literal):
                property_value_py = property_.value.toPython()
                if isinstance(property_value_py, str):
                    text_property_values.append(property_value_py)
            elif isinstance(property_.value, URIRef):
                uri_property_values.append(property_.value)

        if len(text_property_values) > 1:
            # raise ValueError(f"{property_definition_uri}: more than one text property, ambiguous")
            return None

        if len(uri_property_values) > 1:
            # raise ValueError(f"{property_definition_uri}: more than one URI property, ambiguous")
            return None

        return cls(
            text=text_property_values[0] if text_property_values else None,
            uri=uri_property_values[0] if uri_property_values else None,
        )

    @classmethod
    def from_value(cls, value):
        if value is None:
            return None
        elif isinstance(value, cls):
            return value
        elif isinstance(value, str):
            return cls(text=value)
        elif isinstance(value, URIRef):
            return cls(uri=str(value))
        else:
            raise TypeError(type(value))

    def to_rdf(self, *, add_to_resource: Resource, property_uri: URIRef):
        if self.text is not None:
            add_to_resource.add(property_uri, Literal(self.text))
        if self.uri is not None:
            add_to_resource.add(property_uri, self.uri)
