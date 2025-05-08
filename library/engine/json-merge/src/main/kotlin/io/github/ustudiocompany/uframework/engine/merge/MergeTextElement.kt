package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun mergeText(src: DataElement, currentPath: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Text -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = DataElement.Text::class, actual = src)
            .asFailure()
    }
