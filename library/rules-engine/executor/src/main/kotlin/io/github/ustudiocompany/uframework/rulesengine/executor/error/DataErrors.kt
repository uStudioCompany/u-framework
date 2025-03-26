package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public sealed interface DataErrors : RuleEngineError {

    public class Search(cause: Path.Errors) : DataErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of searching."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class Missing(public val source: Source, public val path: Path) : DataErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "The error of searching."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get,
            DETAILS_KEY_PATH to path.value
        )
    }

    public companion object {
        private const val PREFIX = "RULES-ENGINE-DATA-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "context-source-name"
    }
}
