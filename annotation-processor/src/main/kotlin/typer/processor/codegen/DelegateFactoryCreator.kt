package typer.processor.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import typer.api.commands.annotations.Command
import typer.api.commands.annotations.CommandFactory
import typer.internal.core.delegate.DelegateFactory
import typer.processor.utils.*
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import kotlin.reflect.full.functions


@Suppress("DEPRECATION")
object DelegateFactoryCreator {

    private const val OUTPUT_PACKAGE = "typer.impl.internal.factories"

    fun createDelegateFactory(
        element: Element,
        roundEnv: RoundEnvironment,
        processingEnv: ProcessingEnvironment
    ): ClassName {

        val commandAnnotation = element.getAnnotation(Command::class.java)
        if (commandAnnotation != null)
            return createDelegateFactoryForCommand(
                commandAnnotation,
                element as TypeElement,
                roundEnv,
                processingEnv
            )

        val commandFactoryAnnotation = element.getAnnotation(CommandFactory::class.java)
        if (commandFactoryAnnotation != null)
            return createDelegateFactoryForCommandFactory(
                commandFactoryAnnotation,
                element as ExecutableElement,
                roundEnv,
                processingEnv,
            )

        throw IllegalStateException("WTF")
    }

    fun createDelegateFactoryForCommand(
        annotation: Command,
        element: TypeElement,
        roundEnv: RoundEnvironment,
        processingEnv: ProcessingEnvironment,
    ): ClassName {
        if (!element.hasNoArgConstructor) processingEnv.error("Missing no arg constructor", element)

        val delegateClassName = ClassName(processingEnv.packageOfAsString(element), element.simpleName.toString())
        val implName = "${element.simpleName}_DelegateFactory"


        val delegateFactoryProxyImpl = TypeSpec.classBuilder(implName)
            .addSuperinterface(
                DelegateFactory::class.asClassName()
                    .parameterizedBy(element.asClassName())
            )
            .addFunction(
                FunSpec.builder(DelegateFactory::class.functions.first().name)
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(element.asType().asTypeName())
                    .addStatement("return %T()", delegateClassName)
                    .build()
            )
            .build()

        FileSpec.builder(OUTPUT_PACKAGE, implName).addType(delegateFactoryProxyImpl).build()
            .writeTo(File(processingEnv.outputDirectory))

        return ClassName(OUTPUT_PACKAGE, implName)
    }

    fun createDelegateFactoryForCommandFactory(
        annotation: CommandFactory,
        factoryMethod: ExecutableElement,
        roundEnv: RoundEnvironment,
        processingEnv: ProcessingEnvironment,
    ): ClassName {
        if (factoryMethod.takesArguments) processingEnv.error("Factory method must take no arguments", factoryMethod)

        val returnTypeElement = (factoryMethod.returnType as? DeclaredType)?.asElement()
            ?: processingEnv.error("Primitive can not be a command", factoryMethod)

        val delegateClassName =
            ClassName(processingEnv.packageOfAsString(returnTypeElement), returnTypeElement.simpleName.toString())
        val implName = "${returnTypeElement.simpleName}_DelegateFactory"

        val delegateFactoryProxyImpl = TypeSpec
            .classBuilder(implName)
            .addSuperinterface(
                DelegateFactory::class.asClassName()
                    .parameterizedBy(factoryMethod.returnType.asTypeName())
            )
            .addFunction(
                FunSpec.builder("createCommandDelegate")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(delegateClassName)
                    .addStatement(
                        "return %M()",
                        MemberName(processingEnv.packageOfAsString(factoryMethod), factoryMethod.simpleName.toString())
                    )
                    .build()
            )
            .build()

        FileSpec.builder(OUTPUT_PACKAGE, implName).addType(delegateFactoryProxyImpl).build()
            .writeTo(File(processingEnv.outputDirectory))

        return ClassName(OUTPUT_PACKAGE, implName)
    }
}

