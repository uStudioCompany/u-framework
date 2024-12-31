package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.BooleanOperators.entries

public enum class BooleanOperators(
    private val tag: String,
    private val operator: Operator<Boolean>
) : Operator<Boolean> by operator {
    CONTAINS(tag = "contains", operator = ContainsOperator),
    EQ(tag = "eq", operator = EqualOperator),
    IN(tag = "in", operator = InOperator),
    IS_PRESENT(tag = "isPresent", operator = IsPresentOperator),
    ;

    public companion object {

        @JvmStatic
        public fun of(value: String): BooleanOperators = entries.first { it.tag.equals(value, true) }
    }
}
