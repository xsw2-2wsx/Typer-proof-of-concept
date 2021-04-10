package typer.api.commands.annotations

import kotlin.reflect.KClass

/**
 * Denotes a CommandFactory, and its return type as command delegate.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class CommandFactory(val name: String)
