package io.github.ustudiocompany.uframework.rulesengine.executor.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.Context

internal fun Step.Requirement.execute(context: Context): ExecutionResult =
    resultWith {
        val (target) = target.compute(context)
        val (compareWith) = compareWith.compute(context)
        val result = comparator.compare(target, compareWith)
        if (result)
            Success.asNull
        else
            this@execute.errorCode.asSuccess()
    }
