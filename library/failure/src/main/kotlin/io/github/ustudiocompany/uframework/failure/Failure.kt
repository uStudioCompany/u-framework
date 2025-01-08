package io.github.ustudiocompany.uframework.failure

import io.github.ustudiocompany.uframework.failure.Failure.Cause
import io.github.ustudiocompany.uframework.failure.Failure.Details

public interface Failure {
    /**
     * The code of the failure.
     */
    public val code: String

    /**
     * The description of the failure.
     */
    public val description: String get() = ""

    /**
     * The details of the failure.
     */
    public val details: Details get() = Details.NONE

    /**
     * The cause of the failure.
     */
    public val cause: Cause get() = Cause.None

    public sealed interface Cause {

        /**
         * The cause is not present.
         */
        public data object None : Cause

        /**
         * The cause is an exception.
         */
        public data class Exception(public val get: Throwable) : Cause

        /**
         * The cause is a failure.
         */
        public data class Failure(public val get: io.github.ustudiocompany.uframework.failure.Failure) : Cause
    }

    public class Details private constructor(private val items: List<Item>) : List<Details.Item> by items {

        /**
         * Returns the value of the item from details by the specified [key] or null if the item is not found.
         * @param key the key for search.
         * @return the value or null.
         */
        public operator fun get(key: String): String? = items.find { it.key == key }?.value

        /**
         * Concatenates two details.
         * @param details the details to concatenate.
         * @return the new details.
         */
        public operator fun plus(details: Details): Details = Details(this.items + details)

        /**
         * Adds the specified [item] to the details.
         * @param item the item to add.
         * @return the new details.
         */
        public operator fun plus(item: Item): Details = Details(this.items + item)

        override fun toString(): String = items.joinToString(prefix = "[", postfix = "]") { it.toString() }

        public data class Item(public val key: String, public val value: String) {
            override fun toString(): String = "$key=`$value`"
        }

        public companion object {

            /**
             * The empty details.
             */
            public val NONE: Details = Details(emptyList())

            /**
             * Creates a new details with the specified [items].
             * @param items the items to create a new details.
             * @return the new details.
             */
            public fun of(vararg items: Item): Details = of(items.toList())

            /**
             * Creates a new details with the specified [items].
             * @param items the key-value pairs to create a new details.
             * @return the new details.
             */
            public fun of(vararg items: Pair<String, String>): Details =
                of(items.map { Item(key = it.first, value = it.second) })

            /**
             * Creates a new details with the specified [items].
             * @param items the items to create a new details.
             * @return the new details.
             */
            public fun of(items: List<Item>): Details = if (items.isNotEmpty()) Details(items) else NONE
        }
    }

    public companion object
}

/**
 * Returns the root failure of this failure and its causes.
 */
public fun Failure.root(): Failure = fold(initial = { this }) { _, failure -> failure }

/**
 * Returns the full code by concatenating all codes of the failure and its causes using the specified [delimiter].
 * @param delimiter the delimiter to use between codes.
 * @return the full code.
 */
public fun Failure.fullCode(delimiter: String = "."): String =
    fold(initial = { StringBuilder(it.code) }) { acc, failure ->
        acc.append(delimiter)
            .append(failure.code)
    }.toString()

/**
 * Returns all details of the failure and its causes.
 * @return the all details.
 */
public fun Failure.allDetails(): Details {
    val allDetails = fold(initial = { mutableListOf<Details.Item>().apply { addAll(it.details) } }) { acc, failure ->
        acc.apply { addAll(failure.details) }
    }
    return if (allDetails.isEmpty())
        Details.NONE
    else
        Details.of(allDetails)
}

/**
 * Returns all descriptions of the failure and its causes.
 */
public fun Failure.allDescriptions(): List<String> = buildList<String> {
    val result = this
    this@allDescriptions.fold(initial = { result.apply { add(it.description) } }) { acc, failure ->
        acc.apply { add(failure.description) }
    }
}

/**
 * Returns the full description of the failure.
 */
public fun Failure.fullDescription(separator: String = " "): String =
    allDescriptions()
        .joinToString(separator = separator)

/**
 * Returns the exception that caused the failure or null if the failure is not caused by an exception.
 */
public tailrec fun Failure.exceptionOrNull(): Throwable? =
    when (val cause = this.cause) {
        is Cause.None -> null
        is Cause.Exception -> cause.get
        is Cause.Failure -> cause.get.exceptionOrNull()
    }

/**
 * Folds the failure and its causes with the specified [operation] starting with the specified [initial] value.
 */
public inline fun <S> Failure.fold(initial: (Failure) -> S, operation: (acc: S, Failure) -> S): S {
    var accumulator: S = initial(this)
    var next = this.cause
    while (next is Cause.Failure) {
        accumulator = operation(accumulator, next.get)
        next = next.get.cause
    }
    return accumulator
}
