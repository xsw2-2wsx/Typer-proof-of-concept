package typer.api.commands.annotations.args

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Flag(
    val name: Char,
    val description: String,
)
