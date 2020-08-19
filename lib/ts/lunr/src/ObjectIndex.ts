import lunr, {Index} from "lunr";
import {Object} from "@paradicms/models";

export class ObjectIndex {
  private readonly index: Index;
  private readonly objectsByUri: {[index: string]: Object};

  constructor(objects: readonly Object[]) {
    const objectsByUri: {[index: string]: Object} = {};
    this.objectsByUri = objectsByUri;
    this.index = lunr(function() {
      this.field("title");
      this.ref("uri");
      for (const object of objects) {
        this.add({title: object.title, uri: object.uri});
        objectsByUri[object.uri] = object;
      }
    });
  }

  search(query: string): readonly Object[] {
    return this.index.search(query).map(({ref}) => this.objectsByUri[ref]);
  }
}
