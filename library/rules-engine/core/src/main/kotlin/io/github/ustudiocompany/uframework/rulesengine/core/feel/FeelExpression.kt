package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public interface FeelExpression {

    public val value: String

    public fun evaluate(context: Map<Source, DataElement>): ResultK<DataElement, Errors.Evaluate>

    public sealed class Errors : Failure {

        public class Evaluate(public val expression: FeelExpression, message: String? = null) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of evaluating expression: '${expression.value}'" +
                if (message != null) "($message)." else "."
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression.value
            )
        }

        private companion object {
            private const val PREFIX = "FEEL-EXPRESSION-"
            private const val DETAILS_KEY_PATH = "feel-expression"
        }
    }
}
