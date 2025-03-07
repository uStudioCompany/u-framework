package io.github.ustudiocompany.uframework.failure

public interface Causal {

    /**
     * The cause of the failure.
     */
    public val cause: Cause get() = Cause.None
}

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
