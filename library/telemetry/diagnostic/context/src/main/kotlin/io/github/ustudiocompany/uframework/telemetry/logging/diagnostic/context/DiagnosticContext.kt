package io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context

public fun entry(key: String, value: Any?): DiagnosticContext.Entry? =
    value?.let { DiagnosticContext.Entry(key = key, value = it) }

public fun <T, R> entry(key: String, value: T?, transform: (T) -> R & Any): DiagnosticContext.Entry? =
    value?.let { DiagnosticContext.Entry(key = key, value = transform(it)) }

public inline fun <T> withDiagnosticContext(block: DiagnosticContext.() -> T): T = block(DiagnosticContext.Empty)

public inline fun <T> withDiagnosticContext(
    vararg entries: DiagnosticContext.Entry?,
    block: DiagnosticContext.() -> T
): T = block(
    entries.fold(DiagnosticContext.Empty as DiagnosticContext) { acc, element -> acc + element }
)

public inline fun <T> DiagnosticContext.withDiagnosticContext(
    vararg entries: DiagnosticContext.Entry?,
    block: DiagnosticContext.() -> T
): T = block(
    entries.fold(this) { acc, element -> acc + element }
)

public sealed interface DiagnosticContext : Iterable<DiagnosticContext.Entry> {

    public val isEmpty: Boolean

    public val isNotEmpty: Boolean
        get() = !isEmpty

    public operator fun plus(entry: Entry?): DiagnosticContext =
        if (entry != null) Element(head = entry, tail = this) else this

    public fun <R> foldLeft(initial: R, operation: (R, Entry) -> R): R {
        tailrec fun <R> foldLeft(initial: R, element: DiagnosticContext, operation: (R, Entry) -> R): R =
            when (element) {
                is Empty -> initial
                is Element -> foldLeft(operation(initial, element.head), element.tail, operation)
            }

        return foldLeft(initial, this, operation)
    }

    override fun iterator(): Iterator<Entry> = Iter(this)

    public data class Entry(public val key: String, public val value: Any)

    public object Empty : DiagnosticContext {
        override val isEmpty: Boolean = true
        override fun toString(): String = ""
    }

    private class Element(val head: Entry, val tail: DiagnosticContext) : DiagnosticContext {
        override val isEmpty: Boolean = false

        override fun toString(): String {
            tailrec fun StringBuilder.appendElement(element: DiagnosticContext): StringBuilder =
                when (element) {
                    is Empty -> this
                    is Element -> {
                        val head = element.head
                        append(head.key)
                        append("=")
                        append(head.value)

                        val tail = element.tail
                        if (tail is Element) append(", ")
                        appendElement(tail)
                    }
                }

            return buildString { appendElement(this@Element) }
        }
    }

    private class Iter(context: DiagnosticContext) : Iterator<Entry> {
        private var current = context

        override fun hasNext(): Boolean = current is Element

        override fun next(): Entry {
            val element = current
            return if (element is Element) {
                val entry = element.head
                current = element.tail
                entry
            } else
                throw NoSuchElementException()
        }
    }
}
