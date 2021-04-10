package typer.processor.utils

import com.squareup.kotlinpoet.metadata.ImmutableKmProperty
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
class KotlinProperty(
    val metadata: ImmutableKmProperty,
    val enclosingElement: TypeElement,
) {

    val getter: ExecutableElement = enclosingElement
        .findEnclosedByName(metadata.getterSignature!!.name)
        .filterIsInstance<ExecutableElement>()
        .first()

    val setter: ExecutableElement = enclosingElement
        .findEnclosedByName(metadata.setterSignature!!.name)
        .filterIsInstance<ExecutableElement>()
        .first()

    val hasAnnotations: Boolean = enclosingElement
        .findEnclosedByName(metadata.syntheticMethodForAnnotations?.name)
        .filterIsInstance<ExecutableElement>()
        .firstOrNull() != null

    val annotationsMethod = enclosingElement.findEnclosedByName(metadata.syntheticMethodForAnnotations?.name) as? ExecutableElement

    val type: TypeMirror = getter.returnType

    fun <T : Any> isOfType(t: KClass<T>): Boolean =
        t.java.name.toString() == type.toString()

    fun isOfType(type: TypeElement):Boolean =
        type.qualifiedName.toString() != type.qualifiedName.toString()

    fun <T : Annotation> getAnnotation(annotation: KClass<T>): T? =
        enclosingElement
            .findEnclosedByName(metadata.syntheticMethodForAnnotations?.name)
            .filterIsInstance<ExecutableElement>()
            .firstOrNull()
            ?.getAnnotation(annotation.java)



}