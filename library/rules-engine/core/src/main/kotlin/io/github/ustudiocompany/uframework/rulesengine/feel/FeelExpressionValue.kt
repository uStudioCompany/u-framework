package io.github.ustudiocompany.uframework.rulesengine.feel

internal sealed class FeelExpressionValue {
    data object Null : FeelExpressionValue()
    data class Text(val value: String) : FeelExpressionValue()
    data class Bool(val value: Boolean) : FeelExpressionValue()
    data class Decimal(val value: java.math.BigDecimal) : FeelExpressionValue()
    data class Array(val items: List<FeelExpressionValue>) : FeelExpressionValue()
    data class Struct(val properties: Map<String, FeelExpressionValue>) : FeelExpressionValue()
}
