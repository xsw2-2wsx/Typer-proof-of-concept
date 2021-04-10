package typer.api.commands.annotations

/**
 * Marks a class as a command delegate and sets a way for constructing it as no-arg constructor.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Command(val name: String)
