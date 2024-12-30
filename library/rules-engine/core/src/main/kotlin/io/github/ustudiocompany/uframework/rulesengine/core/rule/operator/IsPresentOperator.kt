package io.github.ustudiocompany.uframework.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object IsPresentOperator : AbstractOperator() {

    override fun apply(target: DataElement?, value: DataElement?): Boolean = when (target) {
        null -> false
        is DataElement.Null -> target.compareWith(value)
        is DataElement.Bool -> target.compareWith(value)
        is DataElement.Text -> target.compareWith(value)
        is DataElement.Decimal -> target.compareWith(value)
        is DataElement.Struct -> target.compareWith(value)
        is DataElement.Array -> target.compareWith(value)
    }

    override fun DataElement.Null.compareWith(value: DataElement?): Boolean = true
    override fun DataElement.Bool.compareWith(value: DataElement?): Boolean = true
    override fun DataElement.Text.compareWith(value: DataElement?): Boolean = true
    override fun DataElement.Decimal.compareWith(value: DataElement?): Boolean = true
    override fun DataElement.Struct.compareWith(value: DataElement?): Boolean = true
    override fun DataElement.Array.compareWith(value: DataElement?): Boolean = true
}
