package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator.entries

public enum class Comparator(private val tag: String, private val compareFun: ComparatorFun) {
    CONTAINS(tag = "contains", compareFun = COMPARATOR_CONTAINS),
    EQ(tag = "eq", compareFun = EqualComparator),
    IN(tag = "in", compareFun = COMPARATOR_IN),
    ;

    public fun compare(target: DataElement?, compareWith: DataElement?): Boolean =
        compareFun.compare(target, compareWith)

    public companion object {

        @JvmStatic
        public fun of(value: String): Comparator = entries.first { it.tag.equals(value, true) }
    }
}
