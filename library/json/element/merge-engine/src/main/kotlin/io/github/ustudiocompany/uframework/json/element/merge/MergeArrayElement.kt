package io.github.ustudiocompany.uframework.json.element.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.MergeRule
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.mergeByAttributes
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.wholeListMerge
import io.github.ustudiocompany.uframework.json.element.JsonElement

internal fun mergeArray(
    dst: JsonElement.Array,
    src: JsonElement,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<JsonElement?, MergeError> =
    when (src) {
        is JsonElement.Null -> ResultK.Success.asNull
        is JsonElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = JsonElement.Array::class, actual = src)
            .asFailure()
    }

private fun mergeArray(
    dst: JsonElement.Array,
    src: JsonElement.Array,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<JsonElement?, MergeError> {
    val rule = strategy[currentPath]
        ?: return MergeError.RuleMissing(currentPath).asFailure()

    return when (rule) {
        is MergeRule.WholeListMerge -> wholeListMerge(src)

        is MergeRule.MergeByAttributes ->
            mergeByAttributes(
                attributes = rule.attributes,
                dst = dst,
                src = src,
                strategy = strategy,
                currentPath = currentPath
            )
    }
}
