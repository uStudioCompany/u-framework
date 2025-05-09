package io.github.ustudiocompany.uframework.rulesengine.core.path

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement

public interface Path {

    public val text: String

    public fun searchIn(data: JsonElement): ResultK<JsonElement?, SearchError>

    /**
     * Represents an unexpected search error that occurs while attempting to retrieve data using a JSON path.
     *
     * @property path The JSON path that caused the error.
     * @property exception The exception that triggered the search error.
     */
    public class SearchError(public val path: Path, public val exception: Exception?) : Failure {
        override val code: String = PREFIX + "1"
        override val description: String = "The unexpected error of searching by json-path: '${path.text}'."
        override val cause: Failure.Cause =
            if (exception == null)
                Failure.Cause.None
            else
                Failure.Cause.Exception(exception)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )

        private companion object {
            private const val PREFIX = "SEARCH-BY-JSON-PATH-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
