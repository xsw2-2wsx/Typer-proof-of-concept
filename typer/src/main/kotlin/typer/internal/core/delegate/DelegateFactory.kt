package typer.internal.core.delegate


/**
 * Provides a layer of abstraction from different ways of obtaining an
 * instance of command delegate, like constructor or factory method
 */
fun interface DelegateFactory<T> {
    fun createCommandDelegate(): T
}