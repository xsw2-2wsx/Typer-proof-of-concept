package typer.internal.core


interface CommandNode : Command {
    val subcommands: Set<CommandNode>
}