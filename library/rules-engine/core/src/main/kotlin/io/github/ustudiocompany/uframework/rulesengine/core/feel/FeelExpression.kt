package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public fun interface FeelExpression {

    public fun evaluate(
        context: Map<Source, DataElement>
    ): ResultK<DataElement, Errors.Evaluate>

    public sealed class Errors : Failure {

        public class Evaluate(public val expression: String, message: String? = null) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of evaluating expression: '$expression'" +
                if (message != null) "($message)." else "."
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression
            )
        }

        private companion object {
            private const val PREFIX = "FEEL-EXPRESSION-"
            private const val DETAILS_KEY_PATH = "feel-expression-body"
        }
    }
}
