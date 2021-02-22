import {Namespace} from "rdflib";

export class PARADICMS {
  static NS = Namespace("http://www.paradicms.org/ns#");

  // Properties
  static faceted = PARADICMS.NS("faceted");
  static fullTextSearchable = PARADICMS.NS("fullTextSearchable");

  // Resources
  static PropertyDefinition = PARADICMS.NS("PropertyDefinition");
}
