package typer.internal

import typer.api.CommandLineInterface
import typer.api.commands.Executable
import typer.internal.parsing.Input
import javax.swing.text.html.parser.Parser

abstract class BaseCommandLineInterface(private val parser: Parser) : CommandLineInterface {
    override fun from(input: String): Executable {
        TODO("Not yet implemented")
    }

    abstract fun getCommand(input: Input): Executable


}