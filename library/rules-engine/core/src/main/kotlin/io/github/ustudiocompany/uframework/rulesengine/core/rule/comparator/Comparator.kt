package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator.entries

public enum class Comparator(private val tag: String, private val block: AbstractOperator) {
    CONTAINS(tag = "contains", block = ContainsOperator),
    EQ(tag = "eq", block = EqualOperator),
    IN(tag = "in", block = InOperator),
    IS_PRESENT(tag = "isPresent", block = IsPresentOperator),
    ;

    public fun compare(target: DataElement?, compareWith: DataElement?): Boolean = block(target, compareWith)

    public companion object {

        @JvmStatic
        public fun of(value: String): Comparator = entries.first { it.tag.equals(value, true) }
    }
}
