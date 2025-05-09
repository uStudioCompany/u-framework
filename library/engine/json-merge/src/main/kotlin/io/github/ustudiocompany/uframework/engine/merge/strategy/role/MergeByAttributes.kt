package io.github.ustudiocompany.uframework.engine.merge.strategy.role

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.engine.merge.ID
import io.github.ustudiocompany.uframework.engine.merge.MergeError
import io.github.ustudiocompany.uframework.engine.merge.merge
import io.github.ustudiocompany.uframework.engine.merge.normalize
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.json.element.JsonElement

@Suppress("ReturnCount")
internal fun mergeByAttributes(
    attributes: List<String>,
    dst: JsonElement.Array,
    src: JsonElement.Array,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<JsonElement.Array, MergeError> {
    val srcById = src.grouping(attributes, currentPath)
        .getOrForward { return it }

    val builder = JsonElement.Array.Builder()

    //Update
    val dstIds: List<ID> = dst.map { item ->
        val dstValue = item as? JsonElement.Struct
            ?: return MergeError.Destination.TypeMismatch(
                path = currentPath,
                expected = JsonElement.Struct::class,
                actual = item
            ).asFailure()

        val id: ID = attributes.map { attribute ->
            dstValue[attribute]
                ?: return MergeError.Destination.AttributeForMergeStrategyMissing(currentPath.append(attribute))
                    .asFailure()
        }

        val srcValue = srcById[id]
        val mergedValue = if (srcValue != null)
            merge(dst = dstValue, src = srcValue, strategy = strategy, currentPath = currentPath)
                .getOrForward { return it }
        else
            item

        if (mergedValue != null) builder.add(mergedValue)

        id
    }

    //Append
    srcById.forEach { (id, struct) ->
        if (id !in dstIds)
            struct.normalize()
                ?.let { builder.add(it) }
    }

    return builder.build().asSuccess()
}

private fun JsonElement.Array.grouping(
    attributes: List<String>,
    currentPath: AttributePath
): ResultK<Map<ID, JsonElement>, MergeError> {
    val result: MutableMap<ID, JsonElement> = mutableMapOf()
    this.forEach { item ->
        val struct = item as? JsonElement.Struct
            ?: return MergeError.Source.TypeMismatch(
                path = currentPath,
                expected = JsonElement.Struct::class,
                actual = item
            ).asFailure()

        val id: ID = attributes.map { attribute ->
            struct[attribute]
                ?: return MergeError.Source.AttributeForMergeStrategyMissing(currentPath.append(attribute))
                    .asFailure()
        }

        result[id] = struct
    }
    return result.asSuccess()
}
