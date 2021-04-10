package typer.internal.parsing

import java.util.*

class PeekingIterator<T>(private val delegate: Iterator<T>) : Iterator<T> by delegate {

    private val buffer: MutableList<T> = LinkedList()

    override fun hasNext(): Boolean = buffer.isNotEmpty() || delegate.hasNext()

    override fun next(): T =
        if(buffer.isNotEmpty()) buffer.removeFirst()
        else delegate.next()


    fun peek(): T? =
        if(buffer.isNotEmpty()) buffer[0]
        else if(buffer.isEmpty() && delegate.hasNext()) {
            buffer.add(delegate.next());
            buffer[0]
        }
        else null


    fun peek(amount: Int): List<T> {
        while(buffer.size != amount && delegate.hasNext()) buffer.add(delegate.next())
        return LinkedList(buffer)
    }
}

fun <T> Iterator<T>.toPeekIterator() = PeekingIterator(this)