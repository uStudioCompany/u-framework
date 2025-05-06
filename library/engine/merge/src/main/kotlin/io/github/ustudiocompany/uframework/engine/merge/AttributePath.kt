package io.github.ustudiocompany.uframework.engine.merge

public sealed interface AttributePath {

    public val isEmpty: Boolean

    public fun append(key: String): AttributePath = append(AttributeName(key))

    public fun append(name: AttributeName): AttributePath = Element(name, this)

    public object None : AttributePath {
        override val isEmpty: Boolean = true
        override fun toString(): String = PREFIX
    }

    private class Element(val head: AttributeName, val tail: AttributePath) : AttributePath {

        override val isEmpty: Boolean = false

        override fun toString(): String = buildString {
            append(PREFIX)
            this@Element.foldRight(this) { acc, value ->
                acc.append(".")
                acc.append(value.get)
            }
        }

        override fun hashCode(): Int = foldLeft(HASH_INITIAL) { acc, name -> acc * HASH_SALT + name.get.hashCode() }

        override fun equals(other: Any?): Boolean =
            this === other || (other is AttributePath && equals(self = this, other = other))

        private tailrec fun equals(self: AttributePath, other: AttributePath): Boolean = when {
            self is Element && other is Element ->
                if (self.head == other.head)
                    equals(self.tail, other.tail)
                else
                    NOT_EQUAL

            self is None && other is None -> EQUAL

            else -> NOT_EQUAL
        }

        private fun <R> foldLeft(initial: R, operation: (R, AttributeName) -> R): R =
            foldLeft(initial, this, operation)

        private tailrec fun <R> foldLeft(initial: R, location: AttributePath, operation: (R, AttributeName) -> R): R =
            when (location) {
                is None -> initial
                is Element -> foldLeft(operation(initial, location.head), location.tail, operation)
            }

        private fun <R> foldRight(initial: R, operation: (R, AttributeName) -> R): R =
            foldRight(initial, this, operation)

        private fun <R> foldRight(initial: R, location: AttributePath, operation: (R, AttributeName) -> R): R =
            when (location) {
                is None -> initial
                is Element -> operation(foldRight(initial, location.tail, operation), location.head)
            }

        private companion object {
            private const val HASH_INITIAL = 7
            private const val HASH_SALT = 31
        }
    }

    public companion object {

        public operator fun invoke(vararg keys: String): AttributePath =
            if (keys.isEmpty())
                None
            else
                keys.fold(None as AttributePath) { acc, key ->
                    val name = AttributeName(key)
                    Element(head = name, tail = acc)
                }

        private const val EQUAL = true
        private const val NOT_EQUAL = false
        private const val PREFIX = "$"
    }
}
