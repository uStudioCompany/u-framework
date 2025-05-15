package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal data object IsPresentOperator : AbstractBooleanOperator() {

    override fun compute(target: JsonElement?, value: JsonElement?): Boolean = when (target) {
        null -> false
        is JsonElement.Null -> target.compareWith(value)
        is JsonElement.Bool -> target.compareWith(value)
        is JsonElement.Text -> target.compareWith(value)
        is JsonElement.Decimal -> target.compareWith(value)
        is JsonElement.Struct -> target.compareWith(value)
        is JsonElement.Array -> target.compareWith(value)
    }

    override fun JsonElement.Null.compareWith(value: JsonElement?): Boolean = true
    override fun JsonElement.Bool.compareWith(value: JsonElement?): Boolean = true
    override fun JsonElement.Text.compareWith(value: JsonElement?): Boolean = true
    override fun JsonElement.Decimal.compareWith(value: JsonElement?): Boolean = true
    override fun JsonElement.Struct.compareWith(value: JsonElement?): Boolean = true
    override fun JsonElement.Array.compareWith(value: JsonElement?): Boolean = true
}
