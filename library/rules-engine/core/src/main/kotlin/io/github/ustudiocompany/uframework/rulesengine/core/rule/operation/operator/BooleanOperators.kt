package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

public enum class BooleanOperators(
    public val key: String,
    private val operator: Operator<Boolean>
) : Operator<Boolean> by operator {
    CONTAINS(key = "contains", operator = ContainsOperator),
    EQ(key = "eq", operator = EqualOperator),
    IN(key = "in", operator = InOperator),
    IS_PRESENT(key = "isPresent", operator = IsPresentOperator),
    ;

    public companion object {

        @JvmStatic
        public fun orNull(value: String): Operator<Boolean>? = entries.firstOrNull { it.key.equals(value, true) }
    }
}
