package typer.processor.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isVar
import typer.api.commands.Executable
import typer.api.commands.annotations.Command
import typer.api.commands.annotations.Entrypoint
import typer.api.commands.annotations.args.Arg
import typer.api.commands.annotations.args.Flag
import typer.api.commands.annotations.args.Option
import typer.api.commands.annotations.args.Varargs
import typer.api.exceptions.user.MissingRequiredArgumentException
import typer.api.exceptions.user.MissingRequiredOption
import typer.internal.core.ExecutableFactory
import typer.internal.core.delegate.DelegateFactory
import typer.internal.parsing.Input
import typer.processor.utils.*
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import kotlin.reflect.full.functions

@KotlinPoetMetadataPreview
object ExecutableFactoryCreator {

    private const val OUTPUT_PACKAGE = "typer.impl.internal.factories"
    private const val DELEGATE_FACTORY_PROPERTY_NAME = "delegateFactory"
    private const val DELEGATE_VAL_NAME = "delegate"
    private const val ARGS_VAL_NAME = "arguments"
    private const val OPT_VAL_NAME = "options"
    private const val FLAGS_VAL_NAME = "flags"



    private fun createExecutableFactory(
        delegateElement: TypeElement,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): ClassName {

        val delegateFactory = DelegateFactoryCreator.createDelegateFactory(
            delegateElement,
            roundEnv,
            processingEnv
        )


        val implName = "${delegateElement.simpleName}_ExecutableFactory"
        val factoryFunctionName = ExecutableFactory::class.functions.first().name


        val implBuilder = TypeSpec.classBuilder(implName)
            .addSuperinterface(ExecutableFactory::class)



        val delegateFactoryProperty = PropertySpec.builder(
            DELEGATE_FACTORY_PROPERTY_NAME,
            delegateFactory
        )
            .addModifiers(KModifier.PRIVATE)
            .initializer("%T()", delegateFactory)
            .build()

        implBuilder.addProperty(delegateFactoryProperty)

        val funImplBuilder = FunSpec.builder(factoryFunctionName)
            .addModifiers(KModifier.OVERRIDE)
            .returns(Executable::class)
            .addParameter("input", Input::class)
            .addStatement("val $ARGS_VAL_NAME = input.values")
            .addStatement("$ARGS_VAL_NAME.addAll(input.arguments)")
            .addStatement("val $OPT_VAL_NAME = input.options")
            .addStatement("val $FLAGS_VAL_NAME = input.flags")
            .addStatement("val $DELEGATE_VAL_NAME: %T = " +
                    "${delegateFactoryProperty.name}.${DelegateFactory::class.functions.first().name}()",
                delegateElement.asClassName())

        implPositionalArguments(processingEnv, delegateElement, funImplBuilder)

        implOptions(processingEnv, delegateElement, funImplBuilder)

        implFlags(processingEnv, delegateElement, funImplBuilder)

        implVarargs(processingEnv, delegateElement, funImplBuilder)

        val entryPointMethod = getEntryPointMethod(processingEnv, delegateElement)

        funImplBuilder
            .addStatement("return %T { $DELEGATE_VAL_NAME.${entryPointMethod.simpleName}() }", Executable::class.asClassName())

        implBuilder.addFunction(funImplBuilder.build())

        FileSpec.builder(OUTPUT_PACKAGE, implName)
            .addType(implBuilder.build())
            .build()
            .writeTo(File(processingEnv.outputDirectory))

        return ClassName(OUTPUT_PACKAGE, implName)
    }

    private fun getEntryPointMethod(processingEnv: ProcessingEnvironment, delegateElement: TypeElement): ExecutableElement {
        val annotatedElements = delegateElement.elementsAnnotatedWith(Entrypoint::class)

        if(annotatedElements.isEmpty()) processingEnv.error("No @Entrypoint specified", delegateElement)
        if(annotatedElements.size > 1)
            processingEnv.error("There can be only one @Entrypoint method in a Command class", annotatedElements.last())

        val method = annotatedElements.first() as ExecutableElement

        if(method.takesArguments) processingEnv.error("@Entrypoint annotated method must take no arguments", method)

        return method;
    }

    private fun implPositionalArguments(
        processingEnv: ProcessingEnvironment,
        delegateElement: TypeElement,
        funImplBuilder: FunSpec.Builder) {

        val args = delegateElement
            .propertiesAnnotatedWith(Arg::class)
            .map { ArgumentProperty(it.getAnnotation(Arg::class)!!, it) }
            .sortedBy { it.annotation.index }

        var min = -1;
        args.forEach {
            if(!it.property.metadata.isVar)
                processingEnv.error("@Arg annotated property must be var", it.property.annotationsMethod!!)
            if(!it.property.isOfType(String::class))
                processingEnv.error("@Arg annotated property must be of type ${String::class.java}", it.property.annotationsMethod!!)
            if(it.annotation.index != min+1)
                processingEnv.error("Invalid argument index: ${it.annotation.index}", it.property.annotationsMethod!!)
            min++;
        }

        args.forEach {
            funImplBuilder.addStatement("")

            if(it.annotation.required) funImplBuilder
                .addStatement("$DELEGATE_VAL_NAME.${it.property.metadata.name} = " +
                        "$ARGS_VAL_NAME.removeFirstOrNull()?: throw %T(${it.annotation.index}, %S, %S)",
                    MissingRequiredArgumentException::class.asTypeName(), it.annotation.name, it.annotation.description)

            else funImplBuilder
                .addStatement("if($ARGS_VAL_NAME.isNotEmpty()) " +
                        "$DELEGATE_VAL_NAME.${it.property.metadata.name} = $ARGS_VAL_NAME.removeFirst()")

        }

    }

    private fun implOptions(
        processingEnv: ProcessingEnvironment,
        delegateElement: TypeElement,
        funImplBuilder: FunSpec.Builder
    ) {

        val optionProperties = delegateElement.propertiesAnnotatedWith(Option::class)

        optionProperties.find { !it.isOfType(String::class) }?.apply {
            processingEnv.error("@Options annotated property must be of type ${String::class.java}")
        }

        optionProperties.forEach {
            val optionAnnotation = it.getAnnotation(Option::class)!!

            funImplBuilder.addStatement("")
            if(optionAnnotation.required) funImplBuilder
                .addStatement(
                    "$DELEGATE_VAL_NAME.${it.metadata.name} = $OPT_VAL_NAME[%S]?: throw %T(%S, %S)",
                    optionAnnotation.name,
                    MissingRequiredOption::class.asClassName(),
                    optionAnnotation.name,
                    optionAnnotation.description
                )

            else funImplBuilder
                .addStatement("if($OPT_VAL_NAME.contains(%S)) $DELEGATE_VAL_NAME.${it.metadata.name} = $OPT_VAL_NAME[%S]!!",
                    optionAnnotation.name, optionAnnotation.name)
        }

    }

    fun implFlags(
        processingEnv: ProcessingEnvironment,
        delegateElement: TypeElement,
        fumImplBuilder: FunSpec.Builder,
    ) {

        val flagProperties = delegateElement.propertiesAnnotatedWith(Flag::class)

        flagProperties.find { !it.isOfType(Boolean::class) }?.apply {
            processingEnv.error("@Flag annotated property must be of type ${Boolean::class}", annotationsMethod)
        }


        flagProperties.forEach {
            val flagAnnotation = it.getAnnotation(Flag::class)!!

            fumImplBuilder
                .addStatement("")
                .addStatement("if(flags.remove('${flagAnnotation.name}'))" +
                        "$DELEGATE_VAL_NAME.${it.metadata.name} = true")
        }

    }

    fun implVarargs(
        processingEnv: ProcessingEnvironment,
        delegateElement: TypeElement,
        funImplBuilder: FunSpec.Builder,
    ) {

        val varargsAnnotatedProperties = delegateElement.propertiesAnnotatedWith(Varargs::class)
        if(varargsAnnotatedProperties.isEmpty()) return
        if(varargsAnnotatedProperties.size != 1)
            processingEnv.error("There can be only one @Varargs annotated property in command", delegateElement)

        val prop = varargsAnnotatedProperties.first()

        // TODO: Verify that the property is assignable from List<String>

        funImplBuilder
            .addStatement("")
            .addStatement("$DELEGATE_VAL_NAME.${prop.metadata.name} = $ARGS_VAL_NAME")

    }



    private class ArgumentProperty(val annotation: Arg, val property: KotlinProperty)

}
