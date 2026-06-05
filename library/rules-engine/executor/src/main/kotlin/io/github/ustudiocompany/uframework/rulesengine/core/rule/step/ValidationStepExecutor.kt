package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.operation.OperationCalculationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate

internal fun ValidationStep.execute(
    envVars: EnvVars,
    context: Context
): ResultK<ValidationStep.ErrorCode?, ValidationStepExecutingError> =
    calculate(envVars, context)
        .mapFailure { failure -> ValidationStepExecutingError.OperationCalculation(failure) }
        .flatMapBoolean(
            ifTrue = { Success.asNull },
            ifFalse = { errorCode.asSuccess() }
        )

internal sealed interface ValidationStepExecutingError : BasicRulesEngineError {

    class OperationCalculation(cause: OperationCalculationErrors) : ValidationStepExecutingError {
        override val code: String = PREFIX + "1"
        override val description: String = "An error occurred while performing the validation operation."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "VALIDATION-STEP-EXECUTION-"
    }
}
