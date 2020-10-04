import os
from datetime import datetime
from pathlib import Path
from typing import Optional, Tuple, Union

from paradicms_etl._image_archiver import _ImageArchiver
from paradicms_etl.loaders._buffering_loader import _BufferingLoader
from paradicms_etl.loaders.gui.gui_builder import GuiBuilder
from paradicms_etl.loaders.gui.gui_data_loader import GuiDataLoader
from paradicms_etl.loaders.gui.gui_images_loader import GuiImagesLoader
from paradicms_etl.models._image import _Image
from paradicms_etl.models.image_dimensions import ImageDimensions


class GuiLoader(_BufferingLoader):
    def __init__(
        self,
        *,
        gui: Union[Path, str],
        image_archiver: Optional[_ImageArchiver] = None,
        thumbnail_max_dimensions: Tuple[
            ImageDimensions, ...
        ] = GuiImagesLoader.THUMBNAIL_MAX_DIMENSIONS_DEFAULT,
        **kwds,
    ):
        _BufferingLoader.__init__(self, **kwds)
        self.__gui = gui
        self.__image_archiver = image_archiver
        self.__thumbnail_max_dimensions = thumbnail_max_dimensions

    def _flush(self, models):
        if self.__image_archiver and self.__thumbnail_max_dimensions:
            original_images = []
            other_models = []
            for model in models:
                if isinstance(model, _Image):
                    image = model
                    if image.original_image_uri is None:
                        original_images.append(image)
                else:
                    other_models.append(model)

            gui_images = list(
                GuiImagesLoader(
                    image_archiver=self.__image_archiver,
                    loaded_data_dir_path=self._loaded_data_dir_path,
                    pipeline_id=self._pipeline_id,
                    thumbnail_max_dimensions=self.__thumbnail_max_dimensions,
                ).load(models=original_images)
            )

            models = tuple(gui_images + other_models)

        data_dir_path = self._loaded_data_dir_path / "data"
        data_loader = GuiDataLoader(
            loaded_data_dir_path=data_dir_path, pipeline_id=self._pipeline_id,
        )
        data_loader.load(models=models)
        data_loader.flush()
        self._logger.info("loaded data to %s", data_dir_path)

        gui_builder = GuiBuilder(gui=self.__gui)

        gui_builder.clean()

        gui_out_dir_path = gui_builder.build(data_dir_path=data_dir_path)

        final_dist_dir_path = self._loaded_data_dir_path / "site"
        if final_dist_dir_path.is_dir():
            archive_dist_dir_path = (
                self._loaded_data_dir_path
                / f"site-pre-{datetime.now().isoformat().split('.')[0].replace('-', '').replace(':', '')}"
            )
            self._logger.info(
                "renaming existing final dist directory %s to %s",
                final_dist_dir_path,
                archive_dist_dir_path,
            )
            # rmtree has some issues deleting very long file paths on Windows
            # rename the old directory instead
            os.rename(
                final_dist_dir_path, archive_dist_dir_path,
            )

        self._logger.info("renaming %s to %s", gui_out_dir_path, final_dist_dir_path)
        os.rename(gui_out_dir_path, final_dist_dir_path)
