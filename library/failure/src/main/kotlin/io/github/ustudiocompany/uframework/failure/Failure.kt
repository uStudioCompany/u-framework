package io.github.ustudiocompany.uframework.failure

public interface Failure {
    public val domain: String
    public val number: String
    public val description: String get() = ""
    public val details: Details get() = Details.NONE
    public val cause: Cause get() = Cause.None

    public fun fullCode(): String {
        fun StringBuilder.appendCurrentCode(failure: Failure): StringBuilder = apply {
            if (failure.domain.isNotEmpty()) {
                append(failure.domain)
                append(CODE_DELIMITER)
            }
            append(failure.number)
        }

        tailrec fun StringBuilder.appendCodeFromCause(cause: Cause): StringBuilder =
            when (cause) {
                is Cause.None -> this
                is Cause.Exception -> this
                is Cause.Failure -> {
                    val error = cause.get
                    append(CHAIN_DELIMITER)
                        .appendCurrentCode(error)
                        .appendCodeFromCause(error.cause)
                }
            }

        return StringBuilder()
            .appendCurrentCode(this)
            .appendCodeFromCause(cause)
            .toString()
    }

    public fun first(): Failure {
        tailrec fun getUnderlying(failure: Failure): Failure =
            when (val cause = failure.cause) {
                is Cause.None -> failure
                is Cause.Exception -> failure
                is Cause.Failure -> getUnderlying(cause.get)
            }
        return getUnderlying(this)
    }

    public fun descriptions(): List<String> {
        fun MutableList<String>.appendCurrentDescription(failure: Failure): MutableList<String> = apply {
            add(failure.description)
        }

        tailrec fun MutableList<String>.appendDescriptionFromCause(cause: Cause): MutableList<String> =
            when (cause) {
                is Cause.None -> this
                is Cause.Exception -> this
                is Cause.Failure -> {
                    val error = cause.get
                    appendCurrentDescription(error)
                    appendDescriptionFromCause(error.cause)
                }
            }

        return mutableListOf<String>()
            .appendCurrentDescription(this)
            .appendDescriptionFromCause(cause)
    }

    public fun joinDescriptions(separator: String = " "): String = descriptions().joinToString(separator = separator)

    public fun getException(): Throwable? {
        tailrec fun getException(failure: Failure): Throwable? =
            when (val cause = failure.cause) {
                is Cause.None -> null
                is Cause.Exception -> cause.get
                is Cause.Failure -> getException(cause.get)
            }

        return getException(this)
    }

    public fun fullDetails(): Details {
        tailrec fun MutableList<Details.Item>.appendDetailsFromCause(cause: Cause): List<Details.Item> =
            when (cause) {
                is Cause.None -> this
                is Cause.Exception -> this
                is Cause.Failure -> {
                    val error = cause.get
                    addAll(error.details)
                    appendDetailsFromCause(error.cause)
                }
            }

        val allDetails = buildList<Details.Item> {
            addAll(details)
            appendDetailsFromCause(cause)
        }
        return Details.of(allDetails)
    }

    public sealed interface Cause {

        public data object None : Cause {
            override fun toString(): String = "None"
        }

        public class Exception(public val get: Throwable) : Cause {
            override fun toString(): String = "Exception: $get"
        }

        public class Failure(public val get: io.github.ustudiocompany.uframework.failure.Failure) : Cause {
            override fun toString(): String = "Failure: $get"
        }
    }

    public class Details private constructor(private val items: List<Item>) : List<Details.Item> by items {
        public operator fun plus(details: Details): Details = Details(this.items + details)
        public operator fun plus(item: Item): Details = Details(this.items + item)
        public operator fun get(key: String): String? = items.find { it.key == key }?.value

        override fun toString(): String = items.joinToString(prefix = "[", postfix = "]") { it.toString() }

        public data class Item(public val key: String, public val value: String) {
            override fun toString(): String = "$key=`$value`"
        }

        public companion object {
            public val NONE: Details = Details(emptyList())

            public fun of(vararg items: Item): Details = of(items.toList())

            public fun of(vararg items: Pair<String, String>): Details =
                of(items.map { Item(key = it.first, value = it.second) })

            public fun of(items: List<Item>): Details = if (items.isNotEmpty()) Details(items) else NONE
        }
    }

    private companion object {
        private const val CODE_DELIMITER = "-"
        private const val CHAIN_DELIMITER = "."
    }
}
