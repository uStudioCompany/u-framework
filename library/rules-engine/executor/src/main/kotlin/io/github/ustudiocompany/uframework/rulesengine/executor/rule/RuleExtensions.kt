package io.github.ustudiocompany.uframework.rulesengine.executor.rule

import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isMet
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleExecutionErrors

internal fun Rule.isApplicable(envVars: EnvVars, context: Context) =
    condition.isMet(envVars, context)
        .mapFailure { failure -> RuleExecutionErrors.ConditionEvaluation(ruleId = id, cause = failure).asError() }
