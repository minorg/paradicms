def sanitize_method_name(string: str) -> str:
    return string.replace(" ", "_").lower().encode("ascii", "ignore").decode("ascii")