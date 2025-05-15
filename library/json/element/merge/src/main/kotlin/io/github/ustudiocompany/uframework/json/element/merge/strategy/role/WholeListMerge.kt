package io.github.ustudiocompany.uframework.json.element.merge.strategy.role

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.MergeError
import io.github.ustudiocompany.uframework.json.element.merge.normalize

internal fun wholeListMerge(src: JsonElement): ResultK<JsonElement?, MergeError> =
    src.normalize()
        ?.asSuccess()
        ?: ResultK.Success.asNull
