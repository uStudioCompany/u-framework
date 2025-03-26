package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.data.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun DataScheme.build(context: Context): ResultK<DataElement, RuleEngineError> =
    when (this) {
        is DataScheme.Struct -> properties.toStruct(context)
        is DataScheme.Array -> items.toArray(context)
    }

private fun DataScheme.Property.build(context: Context): ResultK<Pair<String, DataElement>, RuleEngineError> =
    when (this) {
        is DataScheme.Property.Struct -> properties.toStruct(context).map { struct -> name to struct }
        is DataScheme.Property.Array -> items.toArray(context).map { array -> name to array }
        is DataScheme.Property.Element -> value.compute(context).map { value -> name to value }
    }

private fun DataScheme.Item.build(context: Context): ResultK<DataElement, RuleEngineError> =
    when (this) {
        is DataScheme.Item.Struct -> properties.toStruct(context)
        is DataScheme.Item.Array -> items.toArray(context)
        is DataScheme.Item.Element -> value.compute(context)
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
