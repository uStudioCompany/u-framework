package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path

public sealed interface DataError : RuleEngineError {

    public class Search(cause: Path.Errors.Search) : DataError {
        override val code: String = "RULES-ENGINE-DATA-SEARCH"
        override val description: String = "The error of searching."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }
}
