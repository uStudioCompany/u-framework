package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.path.Path

public sealed interface DataError : RuleEngineError {

    public class PathMissing(path: Path, exception: Throwable) : DataError {
        override val code: String = PREFIX + "1"
        override val description: String = "The path: `$path` is missing."
        override val cause: Failure.Cause = Failure.Cause.Exception(exception)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.toString()
        )
    }

    public class PathParsing(path: String) : DataError {
        override val code: String = PREFIX + "2"
        override val description: String = "The error of parsing a path: `$path`."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.toString()
        )
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-DATA-"
        private const val DETAILS_KEY_PATH = "data-path"
    }
}
