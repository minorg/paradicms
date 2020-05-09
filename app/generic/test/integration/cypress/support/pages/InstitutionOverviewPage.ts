import { Page } from "./Page";

export class InstitutionOverviewPage extends Page {
  constructor(readonly institutionUri: string) {
    super();
  }

  readonly relativeUrl = "/institution/" + encodeURIComponent(this.institutionUri);
}