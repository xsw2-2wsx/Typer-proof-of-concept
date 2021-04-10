package typer.processor.utils

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import kotlin.reflect.KClass

val TypeElement.hasNoArgConstructor: Boolean
    get() = ElementFilter.constructorsIn(this.enclosedElements)
        .find { it.parameters.isEmpty() }
        ?.let { true }
        ?: false

fun Collection<TypeElement>.forEachWithoutNoArgConstructor(action: (TypeElement) -> Unit) =
        filter { !it.hasNoArgConstructor }
        .forEach(action)

val ExecutableElement.takesArguments
    get() = parameters.isNotEmpty()

fun Collection<ExecutableElement>.forEachWithArguments(action: (ExecutableElement) -> Unit) = filter { it.takesArguments }.forEach(action)

fun Collection<ExecutableElement>.forEachWithoutArguments(action: (ExecutableElement) -> Unit) = filter { !it.takesArguments }.forEach(action)

@KotlinPoetMetadataPreview
fun <T : Annotation> TypeElement.propertiesAnnotatedWith(annotation: KClass<T>): List<KotlinProperty> =
    kotlinProperties.filter { it.getAnnotation(annotation) != null }

fun <T : Annotation> TypeElement.elementsAnnotatedWith(annotation: KClass<T>) =
    enclosedElements.filter { it.getAnnotation(annotation.java) != null }

@KotlinPoetMetadataPreview
val TypeElement.kotlinProperties: List<KotlinProperty>
    get() = toImmutableKmClass().properties.map { KotlinProperty(it, this) }

fun TypeElement.findEnclosedByName(simpleName: String?): List<Element> =
    enclosedElements.filter { it.simpleName.toString() == simpleName }

fun TypeElement.allSuperInterfaces(env: ProcessingEnvironment): List<TypeMirror> {
    val result: MutableList<TypeMirror> = mutableListOf(this.asType())
    result.addAll(interfaces)

    var toCheckFurther = interfaces
    while(toCheckFurther.isNotEmpty()) {

        toCheckFurther = toCheckFurther
            .mapNotNull { env.typeUtils.asElement(it) as? TypeElement }
            .flatMap { it.interfaces }
            .toMutableList()

        result.addAll(toCheckFurther)

    }

    return result;
}


//fun getCollectionType(type: TypeMirror, processingEnv: ProcessingEnvironment): DeclaredType? {
//    val collectionType = processingEnv.elementUtils.getTypeElement(Collection::class.java.name).asType()
//
//    if(!processingEnv.typeUtils.isAssignable(type, collectionType)) return null
//
//    (type as DeclaredType).
//}