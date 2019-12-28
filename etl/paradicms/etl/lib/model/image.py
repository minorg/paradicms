from datetime import datetime
from typing import Optional

from rdflib.namespace import DCTERMS

from paradicms.etl.lib.model._model import _Model
from paradicms.etl.lib.namespace import EXIF


class Image(_Model):
    @property
    def created(self) -> Optional[datetime]:
        return self._get_single_value(DCTERMS.created, datetime)

    @created.setter
    def created(self, value: datetime):
        self._set_single_value(DCTERMS.created, value)

    @property
    def format(self) -> Optional[str]:
        return self._get_single_value(DCTERMS["format"], str)

    @format.setter
    def format(self, value: str):
        self._set_single_value(DCTERMS["format"], value)

    @property
    def height(self) -> Optional[int]:
        return self._get_single_value(EXIF.height, int)

    @height.setter
    def height(self, value: int):
        self._set_single_value(EXIF.height, value)

    @property
    def modified(self) -> Optional[datetime]:
        return self._get_single_value(DCTERMS.modified, datetime)

    @modified.setter
    def modified(self, value: datetime):
        self._set_single_value(DCTERMS.modified, value)

    @property
    def width(self) -> Optional[int]:
        return self._get_single_value(EXIF.width, int)

    @width.setter
    def width(self, value: int):
        self._set_single_value(EXIF.width, value)
