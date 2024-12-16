package io.github.ustudiocompany.uframework.messaging.message

public interface IncomingMessages<out T> : Iterable<IncomingMessage<T>> {
    public val isEmpty: Boolean

    public object Empty : IncomingMessages<Nothing> {
        private val ITER = listOf<IncomingMessage<Nothing>>().iterator()
        override val isEmpty: Boolean = true
        override fun iterator(): Iterator<IncomingMessage<Nothing>> = ITER
    }
}
