export function checkNotNullish<T>(object: T | null | undefined): T {
  if (object == null) {
    throw new EvalError("object is nullish");
  }
  return object;
}
