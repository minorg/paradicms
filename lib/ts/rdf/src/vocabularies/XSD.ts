import {Namespace} from "rdflib";

export class XSD {
  static NS = Namespace("http://www.w3.org/2001/XMLSchema#");

  static boolean_ = XSD.NS("boolean");
  static string_ = XSD.NS("string");
}
