package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ArgType
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.ErrorCode
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.MergeError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.predicate.isSatisfied

public typealias ExecutionResult = ResultK<Step.ErrorCode?, RuleEngineError>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val provider: DataProvider,
    private val merger: Merger
) {

    public fun execute(context: Context, rules: Rules): ExecutionResult = rules.execute(context)

    private fun Rules.execute(context: Context): ResultK<ErrorCode?, RuleEngineError> {
        for (rule in this) {
            val result = rule.executeIfSatisfied(context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfSatisfied(context: Context): ResultK<ErrorCode?, RuleEngineError> =
        predicate.isSatisfied(context)
            .flatMapBoolean(
                ifTrue = { this.steps.execute(context) },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(context: Context): ResultK<ErrorCode?, RuleEngineError> {
        for (step in this) {
            val result = step.executeIfSatisfied(context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Step.executeIfSatisfied(context: Context): ResultK<ErrorCode?, RuleEngineError> =
        predicate.isSatisfied(context)
            .flatMapBoolean(
                ifTrue = {
                    when (this) {
                        is Step.Call -> this.execute(context)
                        is Step.Requirement -> this.execute(context)
                        is Step.Action -> this.execute(context)
                    }
                },
                ifFalse = { Success.asNull },
            )

    private fun Step.Call.execute(context: Context): ExecutionResult {
        fun List<Step.Call.Arg>.get(argType: ArgType): ResultK<Map<String, DataElement>, RuleEngineError> {
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

        fun Step.Call.buildUri() = result {
            val (pathVariables) = args.get(ArgType.PATH_VARIABLE)
            val (requestParameters) = args.get(ArgType.REQUEST_PARAMETER)
            val builder = UriBuilder(uri)
            builder.pathParams(pathVariables)
            builder.queryParams(requestParameters)
            builder.build()
        }

        val self = this
        return resultWith {
            val (uri) = self.buildUri()
            val (headers) = args.get(ArgType.HEADER)
            val (result) = provider.call(uri, headers).mapFailure { CallError(it) }
            val source = this@execute.result.source
            val action = this@execute.result.action
            context.apply(action, source, result)
        }
    }

    private fun Step.Requirement.execute(context: Context): ExecutionResult =
        resultWith {
            val (target) = target.compute(context)
            val (compareWith) = compareWith.compute(context)
            val result = comparator.compare(target, compareWith)
            if (result)
                Success.asNull
            else
                this@execute.errorCode.asSuccess()
        }

    private fun Step.Action.execute(context: Context): ExecutionResult =
        this.dataScheme.build(context)
            .andThen { value ->
                val source = this@execute.result.source
                val action = this@execute.result.action
                context.apply(action, source, value)
            }

    private fun Context.apply(action: Step.Result.Action, source: Source, value: DataElement): ExecutionResult =
        when (action) {
            Step.Result.Action.PUT -> this.insert(source, value)
            Step.Result.Action.MERGE -> this[source]
                .andThen { origin -> merger.merge(origin, value).mapFailure { MergeError(it) } }
                .andThen { this.update(source, it) }
        }.flatMap { Success.asNull }

    private fun DataScheme.build(context: Context): ResultK<DataElement, RuleEngineError> {
        return when (this) {
            is DataScheme.Struct -> properties.toStruct(context)
            is DataScheme.Array -> items.toArray(context)
        }
    }

    private fun DataScheme.Property.build(context: Context): ResultK<Pair<String, DataElement>, RuleEngineError> {
        return when (this) {
            is DataScheme.Property.Struct -> properties.toStruct(context).map { struct -> name to struct }
            is DataScheme.Property.Array -> items.toArray(context).map { array -> name to array }
            is DataScheme.Property.Element -> value.compute(context).map { value -> name to value }
        }
    }

    private fun DataScheme.Item.build(context: Context): ResultK<DataElement, RuleEngineError> {
        return when (this) {
            is DataScheme.Item.Struct -> properties.toStruct(context)
            is DataScheme.Item.Array -> items.toArray(context)
            is DataScheme.Item.Element -> value.compute(context)
        }
    }

    private fun List<DataScheme.Property>.toStruct(context: Context): ResultK<DataElement.Struct, RuleEngineError> =
        this.traverseTo(
            destination = mutableMapOf<String, DataElement>(),
            transform = { property -> property.build(context) }
        ).map { DataElement.Struct(it) }

    private fun List<DataScheme.Item>.toArray(context: Context): ResultK<DataElement.Array, RuleEngineError> =
        this.traverseTo(
            destination = mutableListOf<DataElement>(),
            transform = { item -> item.build(context) }
        ).map { DataElement.Array(it) }
}
