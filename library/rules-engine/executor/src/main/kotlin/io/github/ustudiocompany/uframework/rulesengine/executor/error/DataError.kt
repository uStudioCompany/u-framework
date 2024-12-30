package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path

public sealed interface DataError : RuleEngineError {

    public class Search(cause: Path.Errors) : DataError {
        override val code: String = PREFIX + "SEARCH"
        override val description: String = "The error of searching."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public companion object {
        private const val PREFIX = "RULES-ENGINE-DATA-"
    }
}
