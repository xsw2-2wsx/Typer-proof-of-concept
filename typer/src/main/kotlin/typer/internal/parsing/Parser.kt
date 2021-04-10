package typer.internal.parsing

interface Parser {
    fun parse(input: String): Input
}