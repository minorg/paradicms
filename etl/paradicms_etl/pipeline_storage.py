from pathlib import Path


class PipelineStorage:
    def __init__(self, *, extracted_data_dir_path: Path, transformed_data_dir_path: Path):
        self.__extracted_data_dir_path = extracted_data_dir_path
        self.__transformed_data_dir_path = transformed_data_dir_path

    @classmethod
    def create(cls, *, data_dir_path: Path, pipeline_id: str):
        return \
            cls(
                extracted_data_dir_path=data_dir_path / pipeline_id / "extracted",
                transformed_data_dir_path=data_dir_path / pipeline_id / "transformed"
            )

    @property
    def extracted_data_dir_path(self) -> Path:
        self.__extracted_data_dir_path.mkdir(parents=True, exist_ok=True)
        return self.__extracted_data_dir_path

    @property
    def transformed_data_dir_path(self) -> Path:
        self.__transformed_data_dir_path.mkdir(parents=True, exist_ok=True)
        return self.__transformed_data_dir_path