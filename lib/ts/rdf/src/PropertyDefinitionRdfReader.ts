import {RdfReader} from "RdfReader";
import {PropertyDefinition} from "@paradicms/models";

export class PropertyDefinitionRdfReader extends RdfReader<PropertyDefinition> {
  read(): PropertyDefinition {
    throw new Error("not implemented");
  }
}
