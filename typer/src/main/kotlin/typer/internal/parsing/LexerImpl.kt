package typer.internal.parsing

import java.util.*

class LexerImpl : Lexer {

    private val tokens: MutableList<Token> = LinkedList()
    private lateinit var chars: PeekingIterator<Char>
    private var state = State.VALUE

    override fun input(input: String): Lexer {
        chars = input.iterator().toPeekIterator()
        while(chars.hasNext()) {
            val char = chars.peek()!!
            if(char.isWhitespace()) { chars.next(); continue }

            if(char == '"') { parseQuotedArgument(); continue }
            if(char == '-') {
                if(chars.peek(2)[1] != '-') parseFlag()
                else parseOption()
                continue
            }
            parseValue()
        }
        return this;
    }

    override fun end(): Lexer {
        tokens.add(Token(Token.Type.EOF, ""))
        return this
    }

    private fun parseQuotedArgument() {
        check(chars.next() == '"') { "No opening quote found" }
        val value = buildString {
            try {
                var nextChar = chars.next()
                while(nextChar != '"') {
                    append(nextChar)
                    nextChar = chars.next()
                }

            } catch (e: Exception) { throw SyntaxException("Unmatched quote") }
        }
        state = State.ARG
        tokens.add(Token(Token.Type.ARGUMENT, value))
    }

    private fun parseFlag() {
        check(chars.next() == '-') { "No - found when parsing flag" }
        if(!chars.hasNext()) throw SyntaxException("Missing flag key - stray '-'")
        check(chars.peek()!! != '-') { "Found second '-'" }

        do {
            val char = chars.next()
            if(char.isWhitespace()) break
            tokens.add(Token(Token.Type.FLAG, char.toString()))
            state = State.ARG
        }
        while (chars.hasNext())
    }

    private fun parseOption() {
        check(chars.next() == '-' && chars.next() == '-') { "Invalid option signature" }

        val value = readString()

        state = State.ARG
        tokens.add(Token(Token.Type.OPTION, value))
    }

    private fun parseValue() {
        val value = readString()
        val type = if(state == State.VALUE) Token.Type.VALUE else Token.Type.ARGUMENT
        tokens.add(Token(type, value))
    }

    private fun readString(): String = buildString {
        while(chars.hasNext()) {
            val char = chars.next()
            if(char.isWhitespace()) break
            append(char)
        }
    }

    override fun consume(): Token = tokens.removeFirst()

    override fun peek(): Token = tokens.first()

    override fun peek(num: Int): List<Token> = tokens.take(num)

    private enum class State {
        ARG,
        VALUE
    }
}