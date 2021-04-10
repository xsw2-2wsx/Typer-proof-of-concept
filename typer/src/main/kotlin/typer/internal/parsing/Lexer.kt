package typer.internal.parsing

interface Lexer {

    fun input(input: String): Lexer

    fun end(): Lexer

    fun consume(): Token

    fun peek(): Token

    fun peek(num: Int): List<Token>
}