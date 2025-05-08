package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun DataElement.merge(src: DataElement, strategy: MergeStrategy): ResultK<DataElement?, MergeError> =
    merge(dst = this, src = src, strategy = strategy, currentPath = AttributePath.None)
