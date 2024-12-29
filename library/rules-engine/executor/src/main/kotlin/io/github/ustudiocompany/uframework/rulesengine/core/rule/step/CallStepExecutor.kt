package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.rule.header.build
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.UriTemplateParams
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.build
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallError

internal fun Step.Call.execute(context: Context, provider: DataProvider, merger: Merger): ExecutionResult =
    predicate.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                val step = this
                resultWith {
                    val (request) = step.buildRequest(context)
                    val (value) = provider.call(request).mapFailure { CallError(it) }
                    val source = step.result.source
                    val action = step.result.action
                    context.update(source, action, value, merger::merge)
                }
            },
            ifFalse = { Success.asNull }
        )

private fun Step.Call.buildRequest(context: Context) = result {
    val (uri) = uri.build(context, params)
    val (headers) = headers.build(context)
    DataProvider.Request(uri, headers)
}

private fun UriTemplate.build(context: Context, params: UriTemplateParams) =
    params.build(context)
        .flatMap { values -> this.build(values) }
