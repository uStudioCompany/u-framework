package io.github.ustudiocompany.uframework.engine.merge.strategy.role

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.engine.merge.MergeError
import io.github.ustudiocompany.uframework.engine.merge.normalize
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun wholeListMerge(src: DataElement): ResultK<DataElement?, MergeError> =
    src.normalize()
        ?.asSuccess()
        ?: ResultK.Success.asNull
