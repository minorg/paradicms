from paradicms.etl.lib.pipeline._pipeline import _Pipeline
from paradicms.etl.lib.pipeline.collectorz.book_collector_extractor import BookCollectorExtractor
from paradicms.etl.lib.pipeline.collectorz.book_collector_transformer import BookCollectorTransformer


class BookCollectorPipeline(_Pipeline):
    def __init__(self, *, export_xml_file_path: str, pipeline_id: str, **kwds):
        _Pipeline.__init__(
            self,
            extractor=BookCollectorExtractor(export_xml_file_path=export_xml_file_path),
            id=pipeline_id,
            transformer=BookCollectorTransformer(),
            **kwds
        )

    @classmethod
    def add_arguments(cls, argument_parser):
        argument_parser.add_argument('--export-xml-file-path', help="path to book-export.xml", required=True)
        argument_parser.add_argument(
            '--pipeline-id',
            help='unique identifier for this pipeline, used to isolate its cache',
            required=True
        )

