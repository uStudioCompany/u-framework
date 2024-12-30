package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator.entries

public enum class Comparator(private val tag: String, private val block: ComparatorFun) {
    CONTAINS(tag = "contains", block = ContainsComparator),
    EQ(tag = "eq", block = EqualComparator),
    IN(tag = "in", block = InComparator),
    IS_PRESENT(tag = "isPresent", block = IsPresentComparator),
    ;

    public fun compare(target: DataElement?, compareWith: DataElement?): Boolean = block(target, compareWith)

    public companion object {

        @JvmStatic
        public fun of(value: String): Comparator = entries.first { it.tag.equals(value, true) }
    }
}
