package typer.internal.parsing

class Input(
    val values: MutableList<String>,
    val arguments: MutableList<String>,
    val options: MutableMap<String, String>,
    val flags: MutableList<Char>,
)