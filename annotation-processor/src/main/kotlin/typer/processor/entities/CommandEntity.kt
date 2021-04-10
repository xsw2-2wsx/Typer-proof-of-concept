package typer.processor.entities

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

class  CommandEntity(
    val name: String,
    val description: String,
    val helpMessage: String,
    val delegateElement: TypeElement,
    val executableFactory: ClassName,
    val parent: KClass<Any>?,
)