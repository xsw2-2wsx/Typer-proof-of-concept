package typer.api.exceptions.user

class MissingRequiredArgumentException(
    val index: Int,
    val name: String,
    val description: String,
) : SyntaxException("Missing a value for: $name - $description")