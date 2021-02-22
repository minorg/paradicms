import {Literal} from "rdflib/lib/tf-types";
import {XSD} from "./vocabularies";

export class LiteralWrapper {
  constructor(readonly literal: Literal) {}

  isBlank() {
    return this.literal.value.trim().length === 0;
  }

  toBoolean(): boolean {
    if (!this.literal.datatype.equals(XSD.boolean_)) {
      throw new RangeError("literal is not a boolean");
    }
    switch (this.literal.value) {
      case "0":
      case "false":
        return false;
      case "1":
      case "true":
        return true;
      default:
        throw new RangeError(this.literal.value);
    }
  }

  toString(): string {
    return this.literal.value;
  }
}
