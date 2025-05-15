package io.github.ustudiocompany.uframework.rulesengine.parser

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules

/**
 * Functional interface for parsing rules from a string input.
 */
public fun interface RulesParser {

    /**
     * Parses the specified input string to produce a result containing rules or error.
     *
     * @param input The input string to be parsed.
     * @return A result of the parsing operation, containing either the successfully parsed [Rules]
     * or [Errors] in case of failure.
     */
    public fun parse(input: String): ResultK<Rules, Errors>

    public sealed class Errors : Failure {

        /**
         * Represents an error that occurs during the process of parsing JSON.
         */
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
