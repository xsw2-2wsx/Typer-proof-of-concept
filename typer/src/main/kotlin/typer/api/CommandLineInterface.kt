package typer.api

import typer.api.commands.Executable

interface CommandLineInterface {
    fun from(input: String): Executable
}