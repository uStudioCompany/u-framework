package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public sealed interface ContextError : RuleEngineError {

    public class SourceMissing(public val source: Source) : ContextError {
        override val code: String = PREFIX + "1"
        override val description: String = "The source `${source.get}` is not found."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    public class SourceAlreadyExists(public val source: Source) : ContextError {
        override val code: String = PREFIX + "2"
        override val description: String = "The source `${source.get}` is already exists."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    public class Merge(public val source: Source, cause: Failure) : ContextError {
        override val code: String = PREFIX + "3"
        override val description: String = "The error of updating the source `${source.get}`."
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
