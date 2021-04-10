package typer.internal.core

import typer.internal.parsing.Input

interface CommandGraph : CommandNode {

    fun getCommand(input: Input): Command

}