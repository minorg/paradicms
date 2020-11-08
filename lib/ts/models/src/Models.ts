import {Collection} from "Collection";

export class Models {
  static indexByInstitutionUri<T extends {institutionUri: string}>(
    models: readonly T[]
  ): {[index: string]: readonly T[]} {
    return models.reduce((map: {[index: string]: T[]}, model: T) => {
      const models = map[model.institutionUri];
      if (!models) {
        map[model.institutionUri] = [model];
      } else {
        models.push(model);
      }
      return map;
    }, {});
  }

  static indexByUri<T extends {uri: string}>(
    models: readonly T[]
  ): {[index: string]: T} {
    return models.reduce((map, model) => {
      map[model.uri] = model;
      return map;
    }, {} as {[index: string]: T});
  }
}
