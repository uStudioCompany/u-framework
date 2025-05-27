package io.github.ustudiocompany.uframework.rulesengine.core.operation

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.OptionalValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.computeOrNull
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation

internal fun <T> Operation<T>.calculate(envVars: EnvVars, context: Context): ResultK<T, CalculateOperationErrors> =
    result {
        val (target) = target.computeOrNull(envVars, context)
            .mapFailure { failure -> CalculateOperationErrors.ComputingTarget(cause = failure) }
        val value = value?.computeOrNull(envVars, context)
            ?.mapFailure { failure -> CalculateOperationErrors.ComputingValue(cause = failure) }
            ?.bind()
        operator.compute(target = target, value = value)
    }

internal sealed interface CalculateOperationErrors : BasicRulesEngineError {

    class ComputingTarget(cause: OptionalValueComputeErrors) : CalculateOperationErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error computing the operator target."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ComputingValue(cause: OptionalValueComputeErrors) : CalculateOperationErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error computing the operator value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "CALCULATE-OPERATION-"
    }
}
