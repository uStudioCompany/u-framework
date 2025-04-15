package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression

public fun interface ExpressionParser {

    public fun parse(expression: String): ResultK<FeelExpression, Errors>

    public sealed class Errors : Failure {

        public class Parsing(public val expression: String, message: String) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing expression: '$expression'. $message"
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression
            )
        }

        private companion object {
            private const val PREFIX = "EXPRESSION-PARSER-"
            private const val DETAILS_KEY_PATH = "expression"
        }
    }
}
