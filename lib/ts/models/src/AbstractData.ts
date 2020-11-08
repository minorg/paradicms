import {Collection} from "./Collection";
import {GuiMetadata} from "./GuiMetadata";
import {Image} from "./Image";
import {Institution} from "./Institution";
import {Object} from "./Object";
import {PropertyDefinition} from "./PropertyDefinition";
import {Models} from "./Models";
import {Objects} from "./Objects";
import {Images} from "./Images";

export abstract class AbstractData {
  readonly collections: readonly Collection[];
  readonly collectionsByInstitutionUri: {
    [index: string]: readonly Collection[];
  };
  private readonly collectionsByUri: {[index: string]: Collection};
  readonly guiMetadata: GuiMetadata | null;
  readonly images: readonly Image[];
  readonly imagesByDepictsUri: {[index: string]: readonly Image[]};
  readonly imagesByInstitutionUri: {[index: string]: readonly Image[]};
  readonly institutions: readonly Institution[];
  private readonly institutionsByUri: {[index: string]: Institution};
  readonly objects: readonly Object[];
  readonly objectsByCollectionUri: {[index: string]: readonly Object[]};
  readonly objectsByInstitutionUri: {[index: string]: readonly Object[]};
  private readonly objectsByUri: {[index: string]: Object};
  readonly propertyDefinitions: readonly PropertyDefinition[];

  protected constructor() {
    this.collections = this.readModels<Collection>("collection");
    this.collectionsByUri = Models.indexByUri(this.collections);
    this.collectionsByInstitutionUri = Models.indexByInstitutionUri(
      this.collections
    );

    const guiMetadata = this.readModels<GuiMetadata>("guiMetadata");
    this.guiMetadata = guiMetadata.length > 0 ? guiMetadata[0] : null;

    this.images = this.readModels<Image>("image");
    this.imagesByDepictsUri = Images.indexByDepictsUri(this.images);
    this.imagesByInstitutionUri = Models.indexByInstitutionUri(this.images);

    this.institutions = this.readModels<Institution>("institution");
    this.institutionsByUri = Models.indexByUri(this.institutions);

    this.objects = this.readModels<Object>("object");
    this.objectsByUri = Models.indexByUri(this.objects);
    this.objectsByCollectionUri = Objects.indexByCollectionUri(this.objects);
    this.objectsByInstitutionUri = Models.indexByInstitutionUri(this.objects);

    this.propertyDefinitions = this.readModels<PropertyDefinition>(
      "propertyDefinition"
    );
  }

  collectionByUri(uri: string): Collection {
    return this.modelByUri(this.collectionsByUri, uri);
  }

  institutionByUri(uri: string): Institution {
    return this.modelByUri(this.institutionsByUri, uri);
  }

  private modelByUri<ModelT>(
    modelsByUri: {[index: string]: ModelT},
    modelUri: string
  ): ModelT {
    const model = modelsByUri[modelUri];
    if (!model) {
      throw new EvalError("no such model " + modelUri);
    }
    return model;
  }

  objectByUri(uri: string): Object {
    return this.modelByUri(this.objectsByUri, uri);
  }

  protected abstract readModels<ModelT>(
    fileBaseName: string
  ): readonly ModelT[];
}
