package io.github.ustudiocompany.uframework.rulesengine.core.path

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public interface Path {

    public val text: String

    public fun searchIn(data: DataElement): ResultK<DataElement?, Errors>

    public sealed interface Errors : Failure {

        /**
         * Represents a search error that occurs while attempting to retrieve data using a JSON path.
         *
         * @property path The JSON path that caused the error.
         * @property exception The exception that triggered the search error.
         */
        public class Search(public val path: Path, public val exception: Exception) : Errors {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of searching by json-path: '${path.text}'."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path.text
            )
        }

        private companion object {
            private const val PREFIX = "JSON-PATH-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
