package io.github.ustudiocompany.uframework.rulesengine.feel

public sealed class FeelValueWrapper {
    public data object Null : FeelValueWrapper()
    public data class Text(val value: String) : FeelValueWrapper()
    public data class Bool(val value: Boolean) : FeelValueWrapper()
    public data class Decimal(val value: java.math.BigDecimal) : FeelValueWrapper()
    public data class Array(val items: List<FeelValueWrapper>) : FeelValueWrapper()
    public data class Struct(val properties: Map<String, FeelValueWrapper>) : FeelValueWrapper()
}
