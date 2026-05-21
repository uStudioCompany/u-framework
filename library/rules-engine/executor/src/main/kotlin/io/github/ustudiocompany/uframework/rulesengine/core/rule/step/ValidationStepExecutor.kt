package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.operation.CalculateOperationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation

internal fun ValidationStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context
): ResultK<ValidationStep.ErrorCode?, ValidationStepExecuteError> {
    val operation = this@executeIfSatisfied
    return resultWith {
        val (isSatisfied) = checkCondition(condition, envVars, context)
        if (isSatisfied)
            calculateOperation(operation, envVars, context)
                .flatMapBoolean(
                    ifTrue = { Success.asNull },
                    ifFalse = { errorCode.asSuccess() }
                )
        else
            Success.asNull
    }
}

private fun checkCondition(condition: Condition, envVars: EnvVars, context: Context) =
    condition.isSatisfied(envVars, context)
        .mapFailure { failure -> ValidationStepExecuteError.CheckingConditionSatisfaction(failure) }

private fun calculateOperation(operation: Operation<Boolean>, envVars: EnvVars, context: Context) =
    operation.calculate(envVars, context)
        .mapFailure { failure -> ValidationStepExecuteError.CalculateOperation(failure) }

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
