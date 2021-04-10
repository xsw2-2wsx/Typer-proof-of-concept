package test

import typer.api.commands.annotations.Command
import typer.api.commands.annotations.Entrypoint
import typer.api.commands.annotations.args.Arg
import typer.api.commands.annotations.args.Flag
import typer.api.commands.annotations.args.Option
import typer.api.commands.annotations.args.Varargs

@Command(name = "xdxd")
class Foo {

    @Arg(0, "cos", "to jest cos")
    var cos: String = ""

    @Arg(1, "something", "this is something")
    var something: String = ""

    @Arg(2, "etwas", "es ist etwas", required = false)
    var etwas: String = ""

    @Option("Option 1", "Some option 1")
    var o1: String = ""

    @Option("Option 2", "Some option 2")
    var o2: String = ""

    @Option("Option 3", "Some option 3", required = true)
    var o3: String = ""

   @Flag('f', "some flag")
   var isF: Boolean = false

    @Varargs("SomeVarargs", "varargsDescription")
    var varargs: List<String> = emptyList()

    @Entrypoint
    fun execute() {
        println("Hello world!")
    }

}