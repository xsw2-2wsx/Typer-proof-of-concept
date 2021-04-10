package typer.internal.core

interface Command {
    val name: String

    val helpMessage: String

    val executableFactory: ExecutableFactory
}