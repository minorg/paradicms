import {
  Collection,
  GuiMetadata,
  Image,
  Images,
  Institution,
  Models,
  Object,
  Objects,
  PropertyDefinition,
} from "@paradicms/models";
import {IndexedFormula} from "rdflib";
import {CollectionRdfReader} from "CollectionRdfReader";
import {GuiMetadataRdfReader} from "GuiMetadataRdfReader";
import {ImageRdfReader} from "ImageRdfReader";
import {InstitutionRdfReader} from "InstitutionRdfReader";
import {ObjectRdfReader} from "ObjectRdfReader";
import {PropertyDefinitionRdfReader} from "PropertyDefinitionRdfReader";

export class Data {
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

  constructor(store: IndexedFormula) {
    this.collections = CollectionRdfReader.readAll(store);
    this.collectionsByUri = Models.indexByUri(this.collections);
    this.collectionsByInstitutionUri = Models.indexByInstitutionUri(
      this.collections
    );

    const guiMetadata = GuiMetadataRdfReader.readAll(store);
    this.guiMetadata = guiMetadata.length > 0 ? guiMetadata[0] : null;

    this.images = ImageRdfReader.readAll(store);
    this.imagesByDepictsUri = Images.indexByDepictsUri(this.images);
    this.imagesByInstitutionUri = Models.indexByInstitutionUri(this.images);

    this.institutions = InstitutionRdfReader.readAll(store);
    this.institutionsByUri = Models.indexByUri(this.institutions);

    this.propertyDefinitions = PropertyDefinitionRdfReader.readAll(store);

    this.objects = ObjectRdfReader.readAll(this.propertyDefinitions, store);
    this.objectsByUri = Models.indexByUri(this.objects);
    this.objectsByCollectionUri = Objects.indexByCollectionUri(this.objects);
    this.objectsByInstitutionUri = Models.indexByInstitutionUri(this.objects);
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
}
