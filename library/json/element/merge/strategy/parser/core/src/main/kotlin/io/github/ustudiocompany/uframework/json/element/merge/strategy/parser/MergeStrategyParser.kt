package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy

public fun interface MergeStrategyParser {

    public fun parse(input: String): ResultK<MergeStrategy, Errors>

    public sealed class Errors : Failure {

        public class Parsing(cause: Failure) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing JSON with merge strategy."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        private companion object {
            private const val PREFIX = "MERGE-STRATEGY-PARSER-"
        }
    }
}
