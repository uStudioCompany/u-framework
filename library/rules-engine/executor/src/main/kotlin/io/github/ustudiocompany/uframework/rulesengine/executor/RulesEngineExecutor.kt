package io.github.ustudiocompany.uframework.rulesengine.executor

import com.jayway.jsonpath.Configuration
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ArgType
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Predicates
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RequirementError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

public typealias ExecutionResult = ResultK<Unit, RuleEngineError>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val configuration: Configuration,
    private val provider: DataProvider,
    private val merger: Merger
) {
    public fun execute(context: Context, rules: Rules): ExecutionResult {
        rules.map { rule ->
            rule.steps.map { step ->
                step.execute(context).getOrForward { return it }
            }
        }
        return Success.asUnit
    }

    private fun Step.execute(context: Context): ExecutionResult =
        this.isFulfilled(context)
            .andThen { isApply ->
                if (isApply)
                    when (this) {
                        is Step.Call -> this.execute(context)
                        is Step.Requirement -> this.execute(context)
                        is Step.Action -> this.execute(context)
                    }
                else
                    Success.asUnit
            }

    private fun Step.Call.execute(context: Context): ExecutionResult {
        fun List<Step.Call.Arg>.get(argType: ArgType): ResultK<Map<String, DataElement>, RuleEngineError> {
            val argValues = mutableMapOf<String, DataElement>()
            forEach { arg ->
                if (arg.type == argType) {
                    val result = arg.value.compute(context, configuration)
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
            val (result) = provider.call(uri, headers)
            val source = this@execute.result.source
            val action = this@execute.result.action
            context.apply(action, source, result)
        }
    }

    private fun Step.Requirement.execute(context: Context): ExecutionResult =
        resultWith {
            val (target) = target.compute(context, configuration)
            val (compareWith) = compareWith.compute(context, configuration)
            val result = comparator.compare(target, compareWith)
            if (result)
                Success.asUnit
            else
                RequirementError(this@execute.errorCode).asFailure()
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
                .andThen { origin -> merger.merge(origin, value) }
                .andThen { this.update(source, it) }
        }

    private fun Step.isFulfilled(context: Context): ResultK<Boolean, RuleEngineError> =
        this.predicate.isFulfilled(context)

    private fun Predicates?.isFulfilled(context: Context): ResultK<Boolean, RuleEngineError> =
        this?.isFulfilled(context) ?: Success.asTrue

    private fun Predicates.isFulfilled(context: Context): ResultK<Boolean, RuleEngineError> =
        fold(true) { acc, predicate ->
            acc && predicate.isFulfilled(context).getOrForward { return it }
        }.asSuccess()

    private fun Predicate.isFulfilled(context: Context): ResultK<Boolean, RuleEngineError> = result {
        val (target) = target.compute(context, configuration)
        val (compareWith) = compareWith.compute(context, configuration)
        comparator.compare(target, compareWith)
    }

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
            is DataScheme.Property.Element -> value.compute(context, configuration).map { value -> name to value }
        }
    }

    private fun DataScheme.Item.build(context: Context): ResultK<DataElement, RuleEngineError> {
        return when (this) {
            is DataScheme.Item.Struct -> properties.toStruct(context)
            is DataScheme.Item.Array -> items.toArray(context)
            is DataScheme.Item.Element -> value.compute(context, configuration)
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
