from paradicms_etl.pipeline._loader import _Loader
from paradicms_etl.pipeline.file_loader import FileLoader
from paradicms_etl.pipeline.fuseki_loader import FusekiLoader


class DefaultLoader(_Loader):
    def __init__(self, **kwds):
        _Loader.__init__(self, **kwds)
        self.__file_loader = FileLoader(**kwds)
        self.__fuseki_loader = FusekiLoader(**kwds)

    def load(self, **kwds):
        self.__file_loader.load(**kwds)
        self.__fuseki_loader.load(**kwds)