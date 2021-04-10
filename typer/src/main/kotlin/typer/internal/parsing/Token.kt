package typer.internal.parsing

class Token(
    val type: Type,
    val value: String,
) {
    enum class Type {
        VALUE,
        ARGUMENT,
        OPTION,
        FLAG,
        EOF,
    }

    override fun toString(): String = "Token: $type - $value"
}