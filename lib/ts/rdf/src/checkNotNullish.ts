import {RdfReaderException} from "./RdfReaderException";

export function checkNotNullish<T>(object: T | null | undefined): T {
  if (object == null) {
    throw new RdfReaderException("object is nullish");
  }
  return object;
}
