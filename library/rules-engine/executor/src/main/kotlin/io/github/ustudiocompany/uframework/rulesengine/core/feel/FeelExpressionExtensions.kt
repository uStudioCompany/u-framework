package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun FeelExpression.evaluateWithContext(context: Context): ResultK<DataElement, FeelExpressionError.Evaluate> =
    evaluate(context.toMap)
        .mapFailure { FeelExpressionError.Evaluate(it) }

internal sealed interface FeelExpressionError : BasicRulesEngineError {

    class Evaluate(cause: FeelExpression.EvaluateError) : FeelExpressionError {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of evaluating the expression."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-FEEL-EXPRESSION-"
    }
}
