package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun JsonElement.merge(src: JsonElement, strategy: MergeStrategy): ResultK<JsonElement?, MergeError> =
    merge(dst = this, src = src, strategy = strategy, currentPath = AttributePath.None)
