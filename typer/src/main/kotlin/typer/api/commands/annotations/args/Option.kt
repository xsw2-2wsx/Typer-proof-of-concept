package typer.api.commands.annotations.args

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Option(
    val name: String,
    val description: String,
    val required: Boolean = false,)
