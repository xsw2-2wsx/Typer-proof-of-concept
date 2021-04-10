package typer.api.commands.annotations.args

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Varargs(
    val name: String,
    val description: String,
    val required: Int = 0,
)
