package io.github.ustudiocompany.uframework.engine.merge.strategy.role

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.engine.merge.MergeError
import io.github.ustudiocompany.uframework.engine.merge.normalize
import io.github.ustudiocompany.uframework.json.element.JsonElement

internal fun wholeListMerge(src: JsonElement): ResultK<JsonElement?, MergeError> =
    src.normalize()
        ?.asSuccess()
        ?: ResultK.Success.asNull
