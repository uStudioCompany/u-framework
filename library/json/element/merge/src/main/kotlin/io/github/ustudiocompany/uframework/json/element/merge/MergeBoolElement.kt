package io.github.ustudiocompany.uframework.json.element.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.path.AttributePath

internal fun mergeBool(src: JsonElement, currentPath: AttributePath): ResultK<JsonElement?, MergeError> =
    when (src) {
        is JsonElement.Null -> ResultK.Success.asNull
        is JsonElement.Bool -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = JsonElement.Bool::class, actual = src)
            .asFailure()
    }
