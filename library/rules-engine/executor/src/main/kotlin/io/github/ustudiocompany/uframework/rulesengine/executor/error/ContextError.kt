package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public sealed interface ContextError : RuleEngineError {

    /**
     * Represents an error indicating that a specific source is missing within a given context.
     *
     * @property source The source that is not found.
     */
    public class SourceMissing(public val source: Source) : ContextError {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    /**
     * Represents an error indicating that a specific source already exists within a given context.
     *
     * @property source The source that already exists.
     */
    public class SourceAlreadyExists(public val source: Source) : ContextError {
        override val code: String = PREFIX + "2"
        override val description: String = "The source '${source.get}' is already exists."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    /**
     * Represents an error that occurs when attempting to merge or update the specified source in the context.
     *
     * @property source The source that caused the merge error.
     * @property cause The underlying failure that led to the merge error.
     */
    public class Merge(public val source: Source, cause: Failure) : ContextError {
        override val code: String = PREFIX + "3"
        override val description: String = "The error of updating the source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-CONTEXT-"
        private const val DETAILS_KEY_SOURCE = "context-source-name"
    }
}
