package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object ContainsComparator : AbstractComparator() {

    override fun invoke(target: DataElement?, value: DataElement?): Boolean = when {
        target is DataElement.Text && value is DataElement.Array -> target in value
        target is DataElement.Array && value is DataElement.Array -> target in value
        else -> false
    }
}
