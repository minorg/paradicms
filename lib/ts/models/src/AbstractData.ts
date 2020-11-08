import {Collection} from "./Collection";
import {GuiMetadata} from "./GuiMetadata";
import {Image} from "./Image";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {PropertyDefinition} from "./PropertyDefinition";

export abstract class AbstractData {
  readonly collections: readonly Collection[];
  readonly guiMetadata: GuiMetadata | null;
  readonly images: readonly Image[];
  readonly institutions: readonly Institution[];
  readonly objects: readonly Object[];
  readonly propertyDefinitions: readonly PropertyDefinition[];

  protected constructor() {
    this.collections = this.readModels<Collection>("collection");
    const guiMetadata = this.readModels<GuiMetadata>("guiMetadata");
    this.guiMetadata = guiMetadata.length > 0 ? guiMetadata[0] : null;
    this.images = this.readModels<Image>("image");
    this.institutions = this.readModels<Institution>("institution");
    this.objects = this.readModels<Object>("object");
    this.propertyDefinitions = this.readModels<PropertyDefinition>(
      "propertyDefinition"
    );
  }

  protected abstract readModels<ModelT>(
    fileBaseName: string
  ): readonly ModelT[];
}
