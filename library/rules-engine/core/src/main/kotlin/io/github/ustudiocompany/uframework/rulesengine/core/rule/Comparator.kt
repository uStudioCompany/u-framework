package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.entries
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.COMPARATOR_CONTAINS
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.COMPARATOR_EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.COMPARATOR_IN
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.ComparatorFun

public enum class Comparator(private val tag: String, private val compareFun: ComparatorFun) {
    CONTAINS(tag = "contains", compareFun = COMPARATOR_CONTAINS),
    EQ(tag = "eq", compareFun = COMPARATOR_EQ),
    IN(tag = "in", compareFun = COMPARATOR_IN),
    ;

    public fun compare(target: DataElement, compareWith: DataElement): Boolean = compareFun.compare(target, compareWith)

    public companion object {

        @JvmStatic
        public fun of(value: String): Comparator = entries.first { it.tag.equals(value, true) }
    }
}
