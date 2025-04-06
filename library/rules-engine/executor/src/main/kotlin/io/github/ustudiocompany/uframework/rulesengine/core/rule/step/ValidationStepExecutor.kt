package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.operation.CalculateOperationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied

internal fun ValidationStep.executeIfSatisfied(
    context: Context
): ResultK<ValidationStep.ErrorCode?, ValidationStepExecuteError> {
    val operation = this@executeIfSatisfied
    return condition.isSatisfied(context)
        .mapFailure { failure ->
            ValidationStepExecuteError.CheckingConditionSatisfaction(failure)
        }
        .flatMapBoolean(
            ifTrue = {
                operation.calculate(context)
                    .mapFailure { failure ->
                        ValidationStepExecuteError.CalculateOperation(failure)
                    }
                    .flatMapBoolean(
                        ifTrue = { Success.asNull },
                        ifFalse = { errorCode.asSuccess() }
                    )
            },
            ifFalse = { Success.asNull }
        )
}

internal sealed interface ValidationStepExecuteError : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : ValidationStepExecuteError {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'Validation' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class CalculateOperation(cause: CalculateOperationErrors) : ValidationStepExecuteError {
        override val code: String = PREFIX + "2"
        override val description: String = "An error occurred while performing the validation operation."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "VALIDATION-STEP-EXECUTION-"
    }
}
