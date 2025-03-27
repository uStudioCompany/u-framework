package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public sealed interface DataErrors : RuleEngineError {

    /**
     * Represents an error occurring when performing a search using a JSON path.
     *
     * @param cause The specific error related to the path causing this search error.
     */
    public class Search(cause: Path.SearchError) : DataErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Data search error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    /**
     * Represents an error indicating that data is missing in a specific source by a specific path.
     *
     * @property source The data source where the data is expected to reside.
     * @property path The path within the source where the data is missing.
     */
    public class Missing(public val source: Source, public val path: Path) : DataErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Data in source '${source.get}' by path '${path.text}' is missing."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get,
            DETAILS_KEY_PATH to path.text
        )
    }

    public companion object {
        private const val PREFIX = "RULES-ENGINE-DATA-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "context-source-name"
    }
}
