package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal abstract class AbstractComparator {
    abstract operator fun invoke(target: DataElement?, value: DataElement?): Boolean
}
