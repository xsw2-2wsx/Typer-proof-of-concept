package typer.processor.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmProperty
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

const val OUTPUT_FILE_OPTION = "kapt.kotlin.generated"

fun ProcessingEnvironment.error(message: String): Nothing {
    messager.printMessage(Diagnostic.Kind.ERROR, message)
    throw IllegalStateException(message)
}

fun ProcessingEnvironment.error(message: String, element: Element?): Nothing {
    messager.printMessage(Diagnostic.Kind.ERROR, message, element)
    throw IllegalStateException(message)
}

fun ProcessingEnvironment.packageOfAsString(element: Element): String = elementUtils.getPackageOf(element).toString()

val ProcessingEnvironment.outputDirectory
    get() = options[OUTPUT_FILE_OPTION]!!

