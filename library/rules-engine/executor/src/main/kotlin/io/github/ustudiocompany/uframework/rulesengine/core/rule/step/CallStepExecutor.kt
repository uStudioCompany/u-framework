package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ArgType
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.UriBuilder
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Step.Call.execute(context: Context, provider: DataProvider, merger: Merger): ExecutionResult =
    predicate.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                val step = this
                resultWith {
                    val (uri) = step.buildUri(context)
                    val (headers) = step.args.get(ArgType.HEADER, context)
                    val (result) = provider.call(uri, headers).mapFailure { CallError(it) }
                    val source = step.result.source
                    val action = step.result.action
                    context.update(source, action, result, merger::merge)
                }
            },
            ifFalse = { Success.asNull }
        )

private fun List<Step.Call.Arg>.get(
    argType: ArgType,
    context: Context
): ResultK<Map<String, DataElement>, RuleEngineError> {
    val argValues = mutableMapOf<String, DataElement>()
    forEach { arg ->
        if (arg.type == argType) {
            val result = arg.value.compute(context)
            if (result.isFailure()) return result
            argValues[arg.name] = result.value
        }
    }
    return argValues.asSuccess()
}

private fun Step.Call.buildUri(context: Context) = result {
    val (pathVariables) = args.get(ArgType.PATH_VARIABLE, context)
    val (requestParameters) = args.get(ArgType.REQUEST_PARAMETER, context)
    val builder = UriBuilder(uri)
    builder.pathParams(pathVariables)
    builder.queryParams(requestParameters)
    builder.build()
}
