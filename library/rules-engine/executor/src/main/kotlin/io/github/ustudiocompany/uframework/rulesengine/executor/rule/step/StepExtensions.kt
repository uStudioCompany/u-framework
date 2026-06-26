package io.github.ustudiocompany.uframework.rulesengine.executor.rule.step

import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isMet
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.error.StepExecutionErrors

internal fun Step.isApplicable(envVars: EnvVars, context: Context) =
    condition.isMet(envVars, context)
        .mapFailure { failure -> StepExecutionErrors.ConditionEvaluation(stepId = id, cause = failure).asError() }
