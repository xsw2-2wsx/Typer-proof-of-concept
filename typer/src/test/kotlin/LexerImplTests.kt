import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import typer.internal.parsing.LexerImpl
import typer.internal.parsing.Token

class LexerImplTests {
    @Test
    fun testLexer() {
        val cmd = """val1 val2 "arg 1" -fa -b arg2 arg3 --name arg4"""
        val lexer = LexerImpl()
        lexer.input(cmd).end()

        lexer.consume().apply {
            assertEquals(Token.Type.VALUE, type)
            assertEquals("val1", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.VALUE, type)
            assertEquals("val2", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.ARGUMENT, type)
            assertEquals("arg 1", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.FLAG, type)
            assertEquals("f", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.FLAG, type)
            assertEquals("a", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.FLAG, type)
            assertEquals("b", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.ARGUMENT, type)
            assertEquals("arg2", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.ARGUMENT, type)
            assertEquals("arg3", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.OPTION, type)
            assertEquals("name", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.ARGUMENT, type)
            assertEquals("arg4", value)
        }

        lexer.consume().apply {
            assertEquals(Token.Type.EOF, type)
            assertEquals("", value)
        }

    }
}