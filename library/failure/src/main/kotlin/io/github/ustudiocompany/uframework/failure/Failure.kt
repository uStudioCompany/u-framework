package io.github.ustudiocompany.uframework.failure

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
         * The cause is not specified.
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
    val allDetails =
        fold(initial = { mutableListOf<Details.Item>().apply { addAll(it.details) } }) { acc, failure ->
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
        is Failure.Cause.None -> null
        is Failure.Cause.Exception -> cause.get
        is Failure.Cause.Failure -> cause.get.exceptionOrNull()
    }

/**
 * Folds the failure and its causes with the specified [operation] starting with the specified [initial] value.
 */
public inline fun <S> Failure.fold(initial: (Failure) -> S, operation: (acc: S, Failure) -> S): S {
    var accumulator: S = initial(this)
    var next = this.cause
    while (next is Failure.Cause.Failure) {
        accumulator = operation(accumulator, next.get)
        next = next.get.cause
    }
    return accumulator
}
