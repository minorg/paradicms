from typing import Dict, Optional, Tuple

import dateparser
from rdflib import Graph, Literal, URIRef
from rdflib.namespace import DCTERMS, FOAF

from paradicms_etl._model import _Model
from paradicms_etl.models.collection import Collection
from paradicms_etl.models.image import Image
from paradicms_etl.models.object import Object
from paradicms_etl.namespace import CMS, PROV
from paradicms_etl.pipeline._transformer import _Transformer

ElementTextTree = Dict[str, Dict[str, str]]


class OmekaClassicTransformer(_Transformer):
    def __init__(self, *,
                 fullsize_max_height_px: int,
                 fullsize_max_width_px: int,
                 institution_kwds: Dict[str, str],
                 square_thumbnail_height_px: int,
                 square_thumbnail_width_px: int,
                 thumbnail_max_height_px: int,
                 thumbnail_max_width_px: int):
        _Transformer.__init__(self)
        # Single _ so we can use getattr
        self._fullsize_max_height_px = fullsize_max_height_px
        self._fullsize_max_width_px = fullsize_max_width_px
        self.__institution_kwds = institution_kwds
        self._square_thumbnail_height_px = square_thumbnail_height_px
        self._square_thumbnail_width_px = square_thumbnail_width_px
        self._thumbnail_max_height_px = thumbnail_max_height_px
        self._thumbnail_max_width_px = thumbnail_max_width_px

    def transform(self, collections, files, items):
        graph = self._new_graph

        institution = self._transform_institution_from_arguments(graph=graph, **self.__institution_kwds)

        transformed_collections_by_id = {}
        for collection in collections:
            transformed_collection = self._transform_collection(graph=graph,
                                                                 omeka_collection=collection)
            if transformed_collection is not None:
                transformed_collections_by_id[collection["id"]] = transformed_collection

        files_by_item_id = {}
        for file_ in files:
            files_by_item_id.setdefault(file_["item"]["id"], []).append(file_)

        for item in items:
            if not item["public"]:
                self._logger.debug("item %s private, skipping", item["id"])
                continue

            try:
                transformed_collection = transformed_collections_by_id[item["collection"]["id"]]
            except KeyError:
                self._logger.debug("item %s in ignored collection, skipping", item["id"])
                continue

            transformed_item = self._transform_item(files_by_item_id=files_by_item_id, graph=graph, item=item)

            try:
                transformed_collection.add_object(transformed_item)
            except ValueError as e:
                self._logger.warning("invalid object from item %d: %s", item["id"], str(e))

        for transformed_collection in transformed_collections_by_id.values():
            institution.add_collection(transformed_collection)

        return graph

    def _get_element_texts_as_tree(self, omeka_resource) -> ElementTextTree:
        result = {}
        for element_text in omeka_resource["element_texts"]:
            text = element_text["text"].strip()
            if not text:
                continue
            element_set_name = element_text["element_set"]["name"]
            element_name = element_text["element"]["name"]
            element_set_dict = result.setdefault(element_set_name, {})
            element_set_dict.setdefault(element_name, []).append(text)
        return result

    def _log_unknown_element_texts(self, element_text_tree: ElementTextTree) -> None:
        for element_set_name in element_text_tree.keys():
            if element_text_tree[element_set_name]:
                self._logger.warn("unknown %s element names: %s", element_set_name,
                                  tuple(element_text_tree[element_set_name]))

    def _transform_collection(self, *, graph: Graph, omeka_collection) -> Optional[Collection]:
        collection = Collection(
            graph=graph,
            uri=URIRef(omeka_collection["url"])
        )
        collection.owner = CMS.inherit
        element_text_tree = self._get_element_texts_as_tree(omeka_collection)
        self._transform_dublin_core_elements(element_text_tree=element_text_tree, model=collection)
        self._log_unknown_element_texts(element_text_tree)
        return collection

    def _transform_dublin_core_elements(self, *, element_text_tree: ElementTextTree,
                                         model: _Model) -> None:
        dc_element_text_tree = element_text_tree.pop("Dublin Core", None)
        if not dc_element_text_tree:
            return

        def is_uri(value: str):
            return value.startswith("http://") or value.startswith("https://")

        # The items JSON from the API has display name element identifiers instead of the Dublin Core URIs,
        # so we have to map back here.
        for key, property in (
            ("Alternative Title", DCTERMS.alternative),
            ("Contributor", DCTERMS.contributor),
            ("Coverage", DCTERMS.coverage),
            ("Creator", DCTERMS.creator),
            ("Date", DCTERMS.date),
            ("Date Created", DCTERMS.created),
            ("Date Submitted", DCTERMS.dateSubmitted),
            ("Description", DCTERMS.description),
            ("Extent", DCTERMS.extent),
            ("Format", DCTERMS["format"]),
            ("Identifier", DCTERMS.identifier),
            ("Is Referenced By", DCTERMS.isReferencedBy),
            ("Language", DCTERMS.language),
            ("Medium", DCTERMS.medium),
            ("Provenance", DCTERMS.provenance),
            ("Publisher", DCTERMS.publisher),
            ("References", DCTERMS.references),
            ("Relation", DCTERMS.relation),
            ("Rights Holder", DCTERMS.rightsHolder),
            ("Source", DCTERMS.source),
            ("Spatial Coverage", DCTERMS.spatial),
            ("Subject", DCTERMS.subject),
            ("Temporal Coverage", DCTERMS.temporal),
            ("Title", DCTERMS.title),
            ("Type", DCTERMS.type),
        ):
            for value in dc_element_text_tree.pop(key, []):
                # assert not value.startswith("http://") and not value.startswith("https://"), value
                model.resource.add(property, Literal(value))

        for key, property in (
            ("License", DCTERMS.license),
            ("Rights", DCTERMS.rights),
        ):
            for value in dc_element_text_tree.pop(key, []):
                if is_uri(value):
                    model.resource.add(property, URIRef(value))
                else:
                    model.resource.add(property, Literal(value))

        if dc_element_text_tree:
            self._logger.warn("unknown Dublin Core element names: %s", tuple(dc_element_text_tree.keys()))

    def _transform_file(self, *, file_, graph: Graph) -> Tuple[Image, ...]:
        """
        Transform a file JSON object into a sequence of images.

        If the sequence is not empty, the original image is guaranteed to be first.
        """
        if not file_["mime_type"].startswith("image/"):
            return tuple()

        images = []
        original_image = None
        for key, url in file_["file_urls"].items():
            if url is None:
                continue
            image = Image(graph=graph, uri=URIRef(url))
            if key == "original":
                file_metadata = file_["metadata"]
                if isinstance(file_metadata, dict):
                    # Some files have no metadata
                    file_metadata_video = file_metadata["video"]
                    if isinstance(file_metadata_video, dict):
                        image.height = file_metadata_video["resolution_y"]
                        assert isinstance(image.height, int)
                        image.width = file_metadata_video["resolution_x"]
                        assert isinstance(image.width, int)
                    else:
                        self._logger.debug("file %s has no resolution in its metadata", file_["id"])
                else:
                    self._logger.debug("file %s has no metadata", file_["id"])
                original_image = image
            else:
                if key == "square_thumbnail":
                    image.height = self._square_thumbnail_height_px
                    image.width = self._square_thumbnail_width_px
                else:
                    image.max_height = getattr(self, "_" + key + "_max_height_px")
                    image.max_width = getattr(self, "_" + key + "_max_width_px")
                original_uri = URIRef(file_["file_urls"]["original"])
                image.resource.add(PROV.wasDerivedFrom, original_uri)
                graph.add((original_uri, FOAF.thumbnail, image.uri))
            image.created = dateparser.parse(file_["added"], settings={"STRICT_PARSING": True})
            image.format = file_["mime_type"]
            image.modified = dateparser.parse(file_["modified"], settings={"STRICT_PARSING": True})
            if key == "original":
                images.insert(0, image)
            else:
                images.append(image)
        assert original_image
        return tuple(images)

    def _transform_item(self, *, files_by_item_id, graph: Graph, item) -> Object:
        object_ = Object(
            graph=graph,
            uri=URIRef(item["url"])
        )
        object_.owner = CMS.inherit
        item_element_text_tree = self._get_element_texts_as_tree(item)
        self._transform_dublin_core_elements(element_text_tree=item_element_text_tree, model=object_)
        self._transform_item_type_metadata(element_text_tree=item_element_text_tree, model=object_)
        self._log_unknown_element_texts(item_element_text_tree)
        for file_ in files_by_item_id.get(item["id"], []):
            images = self._transform_file(file_=file_, graph=graph)
            if not images:
                continue
            original_image = images[0]
            original_image.resource.add(FOAF.depicts, object_.uri)
            object_.resource.add(FOAF.depiction, original_image.uri)
        return object_

    def _transform_item_type_metadata(self, element_text_tree, model):
        # "Item Type Metadata" is a catch-all element set for all user-defined elements.
        itm_element_text_tree = element_text_tree.pop("Item Type Metadata", None)
        if not itm_element_text_tree:
            return
