package io.github.ustudiocompany.uframework.rulesengine.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules

public fun interface RulesParser {

    public fun parse(input: String): ResultK<Rules, Errors>

    public sealed class Errors : Failure {

        public class Parsing(cause: Failure) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing JSON with rules."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        private companion object {
            private const val PREFIX = "RULES-PARSER-"
        }
    }
}
