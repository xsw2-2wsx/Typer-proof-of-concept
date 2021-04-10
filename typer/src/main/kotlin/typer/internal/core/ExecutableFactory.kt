package typer.internal.core

import typer.api.commands.Executable
import typer.internal.parsing.Input

fun interface ExecutableFactory {
    fun createExecutable(input: Input): Executable
}