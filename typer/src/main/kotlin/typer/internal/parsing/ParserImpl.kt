package typer.internal.parsing

import java.util.*

class ParserImpl(private val lexer: Lexer) : Parser {

    override fun parse(input: String): Input {
        lexer.input(input).end()

        val values = LinkedList<String>()
        val arguments = LinkedList<String>()
        val flags = LinkedList<Char>()
        val options = HashMap<String, String>()

        var token = lexer.consume()

        loop@while(true) {
            when(token.type) {
                Token.Type.VALUE -> values.add(token.value)
                Token.Type.FLAG -> flags.add(token.value[0])
                Token.Type.ARGUMENT -> arguments.add(token.value)
                Token.Type.OPTION -> {
                    val argValue = lexer.consume()
                    if(argValue.type != Token.Type.ARGUMENT)
                        throw SyntaxException("No value for option: ${token.value}")
                    options[token.value] = argValue.value
                }
                Token.Type.EOF -> break@loop
            }
            token = lexer.consume()
        }

        return Input(
            values,
            arguments,
            options,
            flags,
        )
    }
}