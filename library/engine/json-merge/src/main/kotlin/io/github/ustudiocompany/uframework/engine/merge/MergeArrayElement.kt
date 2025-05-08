package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.MergeRule
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.mergeByAttributes
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.wholeListMerge
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun mergeArray(
    dst: DataElement.Array,
    src: DataElement,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = DataElement.Array::class, actual = src)
            .asFailure()
    }

private fun mergeArray(
    dst: DataElement.Array,
    src: DataElement.Array,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<DataElement?, MergeError> {
    val rule = strategy[currentPath]
        ?: return MergeError.RuleMissing(currentPath).asFailure()

    return when (rule) {
        is MergeRule.WholeListMerge -> wholeListMerge(src)

        is MergeRule.MergeByAttributes ->
            mergeByAttributes(
                attributes = rule.attributes,
                dst = dst,
                src = src,
                strategy = strategy,
                currentPath = currentPath
            )
    }
}
