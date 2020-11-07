import {Collection} from "./Collection";
import {GuiMetadata} from "./GuiMetadata";
import {Image} from "./Image";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {PropertyDefinition} from "./PropertyDefinition";

export abstract class AbstractData {
  get collections(): readonly Collection[] {
    return this.models<Collection>("collection");
  }

  get guiMetadata(): GuiMetadata | null {
    const models = this.models<GuiMetadata>("guiMetadata");
    return models.length > 0 ? models[0] : null;
  }

  get images(): readonly Image[] {
    return this.models<Image>("image");
  }

  get institutions(): readonly Institution[] {
    return this.models<Institution>("institution");
  }

  protected abstract models<ModelT>(fileBaseName: string): readonly ModelT[];

  get objects(): readonly Object[] {
    return this.models<Object>("object");
  }

  get propertyDefinitions(): readonly PropertyDefinition[] {
    return this.models<PropertyDefinition>("propertyDefinition");
  }
}
