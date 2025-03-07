package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.header.build
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplateParams
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.build
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.context.update
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallStepError

internal fun CallStep.execute(context: Context, callProvider: CallProvider, merger: Merger): ExecutionResult =
    condition.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                val step = this
                resultWith {
                    val (request) = step.buildRequest(context)
                    val (value) = callProvider.call(request).mapFailure { CallStepError(it) }
                    val source = step.result.source
                    val action = step.result.action
                    context.update(source, action, value, merger::merge)
                }
            },
            ifFalse = { Success.asNull }
        )

private fun CallStep.buildRequest(context: Context) = result {
    val (uri) = uri.build(context, params)
    val (headers) = headers.build(context)
    CallProvider.Request(uri, headers)
}

private fun UriTemplate.build(context: Context, params: UriTemplateParams) =
    params.build(context)
        .flatMap { values -> this.build(values) }
