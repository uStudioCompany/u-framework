package io.github.ustudiocompany.uframework.json.element.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy

public fun JsonElement.merge(src: JsonElement, strategy: MergeStrategy): ResultK<JsonElement?, MergeError> =
    merge(dst = this, src = src, strategy = strategy, currentPath = AttributePath.None)
