package io.github.ustudiocompany.uframework.json.path

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface PathParser {

    public fun parse(path: String): ResultK<Path, Errors.Parsing>

    public sealed interface Errors : Failure {

        public class Parsing(path: String, cause: Exception) : Errors {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing json-path: '$path'."
            override val cause: Failure.Cause = Failure.Cause.Exception(cause)
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to path
            )
        }

        private companion object {
            private const val PREFIX = "JSON-PATH-PARSER-"
            private const val DETAILS_KEY_PATH = "json-path"
        }
    }
}
