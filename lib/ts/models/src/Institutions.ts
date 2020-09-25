import {Institution} from "./Institution";
import {Image} from "./Image";
import {JoinedInstitution} from "./JoinedInstitution";

export class Institutions {
  static join(kwds: {
    imagesByDepictsUri: {[index: string]: readonly Image[]};
    institutions: readonly Institution[];
  }): readonly JoinedInstitution[] {
    const {imagesByDepictsUri, institutions} = kwds;
    return institutions.map(institution => {
      return {
        images: imagesByDepictsUri[institution.uri] ?? [],
        name: institution.name,
        rights: institution.rights,
        uri: institution.uri,
      };
    });
  }
}
