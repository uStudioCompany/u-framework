package io.github.ustudiocompany.uframework.rulesengine.executor.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ArgType
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.UriBuilder
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.apply

internal fun Step.Call.execute(context: Context, provider: DataProvider, merger: Merger): ExecutionResult {
    val self = this
    return resultWith {
        val (uri) = self.buildUri(context)
        val (headers) = args.get(ArgType.HEADER, context)
        val (result) = provider.call(uri, headers).mapFailure { CallError(it) }
        val source = this@execute.result.source
        val action = this@execute.result.action
        context.apply(source, action, result, merger)
    }
}

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
