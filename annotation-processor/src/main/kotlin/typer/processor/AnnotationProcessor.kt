package typer.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import typer.api.commands.annotations.Command
import typer.api.commands.annotations.CommandFactory
import typer.api.commands.annotations.Entrypoint
import typer.api.commands.annotations.ParentCommand
import typer.api.commands.annotations.args.Arg
import typer.processor.codegen.ExecutableFactoryCreator
import typer.processor.utils.OUTPUT_FILE_OPTION
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(OUTPUT_FILE_OPTION)
class AnnotationProcessor : AbstractProcessor() {


    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(
        Command::class.java.name,
        CommandFactory::class.java.name,
        Entrypoint::class.java.name,
        ParentCommand::class.java.name,
        Arg::class.java.name,

    )

    @KotlinPoetMetadataPreview
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?
    ): Boolean = try {

        roundEnv!!.getElementsAnnotatedWith(Command::class.java)
            .forEach {
                ExecutableFactoryCreator.createExecutableFactory(
                    it.getAnnotation(Command::class.java),
                    it as TypeElement,
                    processingEnv,
                    roundEnv
                )
            }

        false
    } catch (e: Exception) {
        throw e
    }
}