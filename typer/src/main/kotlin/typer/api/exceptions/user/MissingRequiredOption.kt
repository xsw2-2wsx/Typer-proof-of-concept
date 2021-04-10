package typer.api.exceptions.user

class MissingRequiredOption(
    val name: String,
    val description: String,
) : SyntaxException("Missing a value for required option: $name - $description")