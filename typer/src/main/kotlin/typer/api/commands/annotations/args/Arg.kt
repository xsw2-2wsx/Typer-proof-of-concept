package typer.api.commands.annotations.args

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Arg(
    val index: Int,
    val name: String,
    val description: String,
    val required: Boolean = true,
)
