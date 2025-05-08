package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun mergeDecimal(src: DataElement, currentPath: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Decimal -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = DataElement.Decimal::class, actual = src)
            .asFailure()
    }
