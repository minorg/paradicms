import {Namespace} from "rdflib";

export class PARADICMS {
  static NS = Namespace("http://www.paradicms.org/ns#");

  // Properties
  static collection = PARADICMS.NS("collection");
  static faceted = PARADICMS.NS("faceted");
  static fullTextSearchable = PARADICMS.NS("fullTextSearchable");
  static object = PARADICMS.NS("object");

  // Resources
  static Collection = PARADICMS.NS("Collection");
  static Institution = PARADICMS.NS("Institution");
  static PropertyDefinition = PARADICMS.NS("PropertyDefinition");
}
