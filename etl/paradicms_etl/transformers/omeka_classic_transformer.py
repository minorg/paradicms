from datetime import datetime
from typing import Dict, Optional, Tuple

from paradicms_etl._transformer import _Transformer
from paradicms_etl.models.collection import Collection
from paradicms_etl.models.image import Image
from paradicms_etl.models.image_dimensions import ImageDimensions
from paradicms_etl.models.object import Object
from paradicms_etl.models.property import Property
from paradicms_etl.models.dublin_core_property_definitions import (
    DublinCorePropertyDefinitions,
)
from paradicms_etl.models.rights import Rights
from pyformance import MetricsRegistry
from rdflib import URIRef
from tqdm import tqdm

ElementTextTree = Dict[str, Dict[str, str]]


class OmekaClassicTransformer(_Transformer):
    def __init__(
        self,
        *,
        fullsize_max_height_px: int,
        fullsize_max_width_px: int,
        square_thumbnail_height_px: int,
        square_thumbnail_width_px: int,
        thumbnail_max_height_px: int,
        thumbnail_max_width_px: int,
        **kwds
    ):
        _Transformer.__init__(self, **kwds)
        # Single _ so we can use getattr
        self._fullsize_max_height_px = fullsize_max_height_px
        self._fullsize_max_width_px = fullsize_max_width_px
        self.__institution_kwds = kwds
        self._metrics_registry = MetricsRegistry()
        self._square_thumbnail_height_px = square_thumbnail_height_px
        self._square_thumbnail_width_px = square_thumbnail_width_px
        self._thumbnail_max_height_px = thumbnail_max_height_px
        self._thumbnail_max_width_px = thumbnail_max_width_px
        self.__transform_item_timer = self._metrics_registry.timer("transform_item")
        self.__transform_file_timer = self._metrics_registry.timer("transform_file")

    def transform(self, collections, files, items):
        yield from DublinCorePropertyDefinitions.as_tuple()

        institution = self._transform_institution_from_arguments(
            **self.__institution_kwds
        )
        yield institution

        collection_uris_by_id = {}
        for collection in tqdm(collections, desc="Omeka collections"):
            transformed_collection = self._transform_collection(
                institution_uri=institution.uri, omeka_collection=collection
            )
            if transformed_collection is None:
                continue
            yield transformed_collection
            collection_uris_by_id[collection["id"]] = transformed_collection.uri

        files_by_item_id = {}
        for file_ in tqdm(files, desc="Omeka files"):
            files_by_item_id.setdefault(file_["item"]["id"], []).append(file_)

        for item in tqdm(items, desc="Omeka items"):
            if not item["public"]:
                self._logger.debug("item %s private, skipping", item["id"])
                continue

            with self.__transform_item_timer.time():
                transformed_item = self._transform_item(
                    collection_uris_by_id=collection_uris_by_id,
                    institution_uri=institution.uri,
                    item=item,
                )
            if transformed_item is None:
                continue

            yield transformed_item

            for file_ in files_by_item_id.get(item["id"], []):
                with self.__transform_file_timer.time():
                    transformed_files = self._transform_file(
                        file_=file_,
                        institution_uri=institution.uri,
                        object_uri=transformed_item.uri,
                    )
                yield from transformed_files

        for key, value in self._metrics_registry.dump_metrics().items():
            self._logger.info("%s: %s", key, value)

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

    def _get_title(
        self, properties: Tuple[Property, ...]
    ) -> Tuple[str, Tuple[Property, ...]]:
        for title_property_definition in (
            DublinCorePropertyDefinitions.TITLE,
            DublinCorePropertyDefinitions.ALTERNATIVE_TITLE,
        ):
            for property_i, property_ in enumerate(properties):
                if property_.uri == title_property_definition.uri:
                    remaining_properties = list(properties[:property_i]) + list(
                        properties[property_i + 1 :]
                    )
                    assert len(remaining_properties) == len(properties) - 1
                    title = property_.value
                    return title, remaining_properties
        raise NotImplementedError("no title property")

    def _log_unknown_element_texts(self, element_text_tree: ElementTextTree) -> None:
        for element_set_name in element_text_tree.keys():
            if element_text_tree[element_set_name]:
                self._logger.warn(
                    "unknown %s element names: %s",
                    element_set_name,
                    tuple(element_text_tree[element_set_name]),
                )

    def _transform_collection(
        self, *, institution_uri: URIRef, omeka_collection
    ) -> Optional[Collection]:
        element_text_tree = self._get_element_texts_as_tree(omeka_collection)
        properties = self._transform_dublin_core_elements(
            element_text_tree=element_text_tree
        )
        self._log_unknown_element_texts(element_text_tree)
        title, properties = self._get_title(properties)
        return Collection(
            institution_uri=institution_uri,
            properties=properties,
            title=title,
            uri=URIRef(omeka_collection["url"]),
        )

    def _transform_dublin_core_elements(
        self, *, element_text_tree: ElementTextTree
    ) -> Tuple[Property, ...]:
        dc_element_text_tree = element_text_tree.pop("Dublin Core", None)
        if not dc_element_text_tree:
            return ()

        # def is_uri(value: str):
        #     return value.startswith("http://") or value.startswith("https://")
        #
        # The items JSON from the API has display name element identifiers instead of the Dublin Core URIs,
        # so we have to map back here.
        properties = set()
        for key, property_definition in (
            ("Alternative Title", DublinCorePropertyDefinitions.ALTERNATIVE_TITLE),
            ("Contributor", DublinCorePropertyDefinitions.CONTRIBUTOR),
            ("Coverage", DublinCorePropertyDefinitions.COVERAGE),
            ("Creator", DublinCorePropertyDefinitions.CREATOR),
            ("Date", DublinCorePropertyDefinitions.DATE),
            ("Date Created", DublinCorePropertyDefinitions.DATE_CREATED),
            ("Date Submitted", DublinCorePropertyDefinitions.DATE_SUBMITTED),
            ("Description", DublinCorePropertyDefinitions.DESCRIPTION),
            ("Extent", DublinCorePropertyDefinitions.EXTENT),
            ("Format", DublinCorePropertyDefinitions.FORMAT),
            ("Identifier", DublinCorePropertyDefinitions.IDENTIFIER),
            ("Is Referenced By", DublinCorePropertyDefinitions.IS_REFERENCED_BY),
            ("Language", DublinCorePropertyDefinitions.LANGUAGE),
            ("License", DublinCorePropertyDefinitions.LICENSE),
            ("Medium", DublinCorePropertyDefinitions.MEDIUM),
            ("Provenance", DublinCorePropertyDefinitions.PROVENANCE),
            ("Publisher", DublinCorePropertyDefinitions.PUBLISHER),
            ("References", DublinCorePropertyDefinitions.IS_REFERENCED_BY),
            ("Relation", DublinCorePropertyDefinitions.RELATION),
            ("Rights", DublinCorePropertyDefinitions.RIGHTS),
            ("Rights Holder", DublinCorePropertyDefinitions.RIGHTS_HOLDER),
            ("Source", DublinCorePropertyDefinitions.SOURCE),
            ("Spatial Coverage", DublinCorePropertyDefinitions.SPATIAL),
            ("Subject", DublinCorePropertyDefinitions.SUBJECT),
            ("Temporal Coverage", DublinCorePropertyDefinitions.TEMPORAL),
            ("Title", DublinCorePropertyDefinitions.TITLE),
            ("Type", DublinCorePropertyDefinitions.TYPE),
        ):
            for value in dc_element_text_tree.pop(key, []):
                properties.add(Property(property_definition, value))

        if dc_element_text_tree:
            self._logger.warn(
                "unknown Dublin Core element names: %s",
                tuple(dc_element_text_tree.keys()),
            )

        return tuple(properties)

    def _transform_file(
        self, *, institution_uri: URIRef, file_, object_uri: URIRef
    ) -> Tuple[Image, ...]:
        """
        Transform a file JSON object into a sequence of images.

        If the sequence is not empty, the original image is guaranteed to be first.
        """
        if not file_["mime_type"].startswith("image/"):
            return tuple()

        file_exact_dimensions = None
        file_metadata = file_["metadata"]
        if isinstance(file_metadata, dict):
            # Some files have no metadata
            file_metadata_video = file_metadata["video"]
            if isinstance(file_metadata_video, dict):
                file_exact_dimensions = ImageDimensions(
                    height=file_metadata_video["resolution_y"],
                    width=file_metadata_video["resolution_x"],
                )
            else:
                self._logger.debug(
                    "file %s has no resolution in its metadata", file_["id"]
                )
        else:
            self._logger.debug("file %s has no metadata", file_["id"])

        file_added = datetime.fromisoformat(file_["added"])
        file_modified = datetime.fromisoformat(file_["modified"])

        images = []
        for key, url in file_["file_urls"].items():
            if url is None:
                continue
            exact_dimensions = max_dimensions = None
            if key == "original":
                exact_dimensions = file_exact_dimensions
                original_image_uri = None
            else:
                if key == "square_thumbnail":
                    exact_dimensions = ImageDimensions(
                        height=self._square_thumbnail_height_px,
                        width=self._square_thumbnail_width_px,
                    )
                else:
                    max_dimensions = ImageDimensions(
                        height=getattr(self, "_" + key + "_max_height_px"),
                        width=getattr(self, "_" + key + "_max_width_px"),
                    )
                original_image_uri = URIRef(file_["file_urls"]["original"])

            image = Image.create(
                created=file_added,
                depicts_uri=object_uri,
                exact_dimensions=exact_dimensions,
                format=file_["mime_type"],
                institution_uri=institution_uri,
                max_dimensions=max_dimensions,
                modified=file_modified,
                original_image_uri=original_image_uri,
                uri=URIRef(url),
            )
            if key == "original":
                images.insert(0, image)
            else:
                images.append(image)
        assert any(image for image in images if image.original_image_uri is None)
        return tuple(images)

    def _transform_item(
        self, *, collection_uris_by_id: Dict[int, URIRef], institution_uri: URIRef, item
    ) -> Optional[Object]:
        try:
            collection_uri = collection_uris_by_id[item["collection"]["id"]]
        except KeyError:
            # Item belongs to a collection that was not transformed, ignore it
            return None

        item_element_text_tree = self._get_element_texts_as_tree(item)
        properties = set()
        for property_ in self._transform_dublin_core_elements(
            element_text_tree=item_element_text_tree
        ):
            properties.add(property_)
        for property_ in self._transform_item_type_metadata(
            element_text_tree=item_element_text_tree
        ):
            properties.add(property_)
        properties = tuple(properties)
        self._log_unknown_element_texts(item_element_text_tree)
        title, properties = self._get_title(properties)
        return Object(
            collection_uris=(collection_uri,),
            institution_uri=institution_uri,
            properties=properties,
            # rights=Rights.from_properties(properties),
            title=title,
            uri=URIRef(item["url"]),
        )

    def _transform_item_type_metadata(self, element_text_tree) -> Tuple[Property, ...]:
        # "Item Type Metadata" is a catch-all element set for all user-defined elements.
        element_text_tree.pop("Item Type Metadata", None)
        return ()
