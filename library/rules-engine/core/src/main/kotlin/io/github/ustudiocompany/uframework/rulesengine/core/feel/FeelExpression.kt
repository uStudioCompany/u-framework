package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public interface FeelExpression {

    public val text: String

    public fun evaluate(context: Map<Source, DataElement>): ResultK<DataElement, EvaluateError>

    /**
     * Represents an evaluation error when processing a FEEL expression.
     *
     * @property expression The expression that caused the evaluation error.
     */
    public class EvaluateError(public val expression: FeelExpression, message: String? = null) : Failure {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of evaluating expression: '${expression.text}'" +
            if (message != null) "($message)." else "."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to expression.text
        )

        private companion object {
            private const val PREFIX = "EVALUATE-FEEL-EXPRESSION-"
            private const val DETAILS_KEY_PATH = "feel-expression"
        }
    }
}
