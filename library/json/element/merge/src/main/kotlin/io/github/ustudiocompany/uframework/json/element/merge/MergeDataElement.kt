package io.github.ustudiocompany.uframework.json.element.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy

internal fun merge(
    dst: JsonElement,
    src: JsonElement,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<JsonElement?, MergeError> =
    when (dst) {
        is JsonElement.Null -> src.normalize().asSuccess()
        is JsonElement.Bool -> mergeBool(src = src, currentPath = currentPath)
        is JsonElement.Text -> mergeText(src = src, currentPath = currentPath)
        is JsonElement.Decimal -> mergeDecimal(src = src, currentPath = currentPath)
        is JsonElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
        is JsonElement.Struct -> mergeStruct(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
    }
