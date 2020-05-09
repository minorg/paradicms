import { Page } from "./Page";
import * as qs from "qs";

export class SearchResultsPage extends Page {
  constructor(readonly text: string) {
    super();
  }

  readonly relativeUrl = "/search?" + qs.stringify({text: this.text});
}