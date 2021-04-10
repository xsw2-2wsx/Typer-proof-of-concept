package typer.api.exceptions.internal

class CommandExecutionException(
    commandName: String,
    cause: Throwable,
    message: String? = null,
) : InternalException(message?: "$commandName command has encountered an exception while executing - $cause", cause)