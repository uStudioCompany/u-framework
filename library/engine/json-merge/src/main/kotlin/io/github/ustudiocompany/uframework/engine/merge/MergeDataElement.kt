package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun merge(
    dst: DataElement,
    src: DataElement,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<DataElement?, MergeError> =
    when (dst) {
        is DataElement.Null -> src.normalize().asSuccess()
        is DataElement.Bool -> mergeBool(src = src, currentPath = currentPath)
        is DataElement.Text -> mergeText(src = src, currentPath = currentPath)
        is DataElement.Decimal -> mergeDecimal(src = src, currentPath = currentPath)
        is DataElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
        is DataElement.Struct -> mergeStruct(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
    }
