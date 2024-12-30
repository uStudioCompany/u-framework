package io.github.ustudiocompany.uframework.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.rule.operator.Operators.entries

public enum class Operators(private val tag: String, private val operator: Operator) : Operator by operator {
    CONTAINS(tag = "contains", operator = ContainsOperator),
    EQ(tag = "eq", operator = EqualOperator),
    IN(tag = "in", operator = InOperator),
    IS_PRESENT(tag = "isPresent", operator = IsPresentOperator),
    ;

    public companion object {

        @JvmStatic
        public fun of(value: String): Operators = entries.first { it.tag.equals(value, true) }
    }
}
