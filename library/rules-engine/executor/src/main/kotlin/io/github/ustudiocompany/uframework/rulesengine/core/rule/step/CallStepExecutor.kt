package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Arg
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.UriBuilder
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallError

internal fun Step.Call.execute(context: Context, provider: DataProvider, merger: Merger): ExecutionResult =
    predicate.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                val step = this
                resultWith {
                    val (uri) = step.buildUri(context)
                    val (headers) = step.args.build(context, Arg.Type.HEADER)
                    val (result) = provider.call(uri, headers).mapFailure { CallError(it) }
                    val source = step.result.source
                    val action = step.result.action
                    context.update(source, action, result, merger::merge)
                }
            },
            ifFalse = { Success.asNull }
        )

private fun Step.Call.buildUri(context: Context) = result {
    val (pathVariables) = args.build(context, Arg.Type.PATH_VARIABLE)
    val (requestParameters) = args.build(context, Arg.Type.REQUEST_PARAMETER)
    val builder = UriBuilder(uri)
    builder.pathParams(pathVariables)
    builder.queryParams(requestParameters)
    builder.build()
}
