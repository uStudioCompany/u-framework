package io.github.ustudiocompany.uframework.rulesengine.executor.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.apply

internal fun Step.Action.execute(context: Context, merger: Merger): ExecutionResult =
    this.dataScheme.build(context)
        .andThen { value ->
            val source = this@execute.result.source
            val action = this@execute.result.action
            context.apply(source, action, value, merger)
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
