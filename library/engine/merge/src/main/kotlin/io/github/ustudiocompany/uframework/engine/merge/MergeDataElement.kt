@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

private typealias ID = List<DataElement>

public fun DataElement.merge(src: DataElement, strategy: MergeStrategy): ResultK<DataElement?, MergeError> =
    merge(dst = this, src = src, strategy = strategy, path = AttributePath.None)

private fun merge(
    dst: DataElement,
    src: DataElement,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> =
    when (dst) {
        is DataElement.Null -> src.normalize().asSuccess()
        is DataElement.Bool -> mergeBool(src = src, path = path)
        is DataElement.Text -> mergeText(src = src, path = path)
        is DataElement.Decimal -> mergeDecimal(src = src, path = path)
        is DataElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, path = path)
        is DataElement.Struct -> mergeStruct(dst = dst, src = src, strategy = strategy, path = path)
    }

private fun mergeBool(src: DataElement, path: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Bool -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = path, expected = DataElement.Bool::class, actual = src)
            .asFailure()
    }

private fun mergeText(src: DataElement, path: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Text -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = path, expected = DataElement.Text::class, actual = src)
            .asFailure()
    }

private fun mergeDecimal(src: DataElement, path: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Decimal -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = path, expected = DataElement.Decimal::class, actual = src)
            .asFailure()
    }

private fun mergeArray(
    dst: DataElement.Array,
    src: DataElement,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Array -> mergeArray(dst = dst, src = src, strategy = strategy, path = path)
        else -> MergeError.Source.TypeMismatch(path = path, expected = DataElement.Array::class, actual = src)
            .asFailure()
    }

private fun mergeArray(
    dst: DataElement.Array,
    src: DataElement.Array,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> {
    val rule = strategy[path]
        ?: return MergeError.RuleMissing(path).asFailure()

    return when (rule) {
        is MergeRule.WholeListMerge -> wholeListMerge(src)

        is MergeRule.MergeByAttributes ->
            mergeByAttributes(
                attributes = rule.attributes,
                dst = dst,
                src = src,
                strategy = strategy,
                path = path
            )
    }
}

private fun mergeStruct(
    dst: DataElement.Struct,
    src: DataElement,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Struct -> mergeStruct(dst = dst, src = src, strategy = strategy, path = path)
        else -> MergeError.Source.TypeMismatch(path = path, expected = DataElement.Struct::class, actual = src)
            .asFailure()
    }

private fun mergeStruct(
    dst: DataElement.Struct,
    src: DataElement.Struct,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> {
    val builder = DataElement.Struct.Builder()

    //Update
    dst.forEach { (key, oldValue) ->
        val newValue = src[key]
        if (newValue != null)
            merge(dst = oldValue, src = newValue, strategy = strategy, path = path.append(key))
                .getOrForward { return it }
                ?.let { mergedValue -> builder[key] = mergedValue }
        else
            oldValue
    }

    //Append
    src.forEach { (key, value) ->
        if (key !in dst)
            value.normalize()
                ?.let { builder[key] = it }
    }

    return if (builder.hasProperties)
        builder.build().asSuccess()
    else
        ResultK.Success.asNull
}

private fun wholeListMerge(src: DataElement): ResultK<DataElement?, MergeError> =
    src.normalize()
        ?.asSuccess()
        ?: ResultK.Success.asNull

private fun mergeByAttributes(
    attributes: List<String>,
    dst: DataElement.Array,
    src: DataElement.Array,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement.Array, MergeError> {
    val srcById = src.grouping(attributes, path)
        .getOrForward { return it }

    val builder = DataElement.Array.Builder()

    //Update
    val dstIds: List<ID> = dst.map { item ->
        val dstValue = item as? DataElement.Struct
            ?: return MergeError.Destination.TypeMismatch(
                path = path,
                expected = DataElement.Struct::class,
                actual = item
            ).asFailure()

        val id: ID = attributes.map { attribute ->
            dstValue[attribute]
                ?: return MergeError.Destination.AttributeForMergeStrategyMissing(path.append(attribute))
                    .asFailure()
        }

        val srcValue = srcById[id]
        val mergedValue = if (srcValue != null)
            merge(dst = dstValue, src = srcValue, strategy = strategy, path = path)
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

private fun DataElement.Array.grouping(
    attributes: List<String>,
    path: AttributePath
): ResultK<Map<ID, DataElement>, MergeError> {
    val result: MutableMap<ID, DataElement> = mutableMapOf()
    this.forEach { item ->
        val struct = item as? DataElement.Struct
            ?: return MergeError.Source.TypeMismatch(path = path, expected = DataElement.Struct::class, actual = item)
                .asFailure()

        val id: ID = attributes.map { attribute ->
            struct[attribute]
                ?: return MergeError.Source.AttributeForMergeStrategyMissing(path.append(attribute))
                    .asFailure()
        }

        result[id] = struct
    }
    return result.asSuccess()
}

private fun DataElement.normalize(): DataElement? {
    return when (this) {
        is DataElement.Null -> null
        is DataElement.Bool -> this
        is DataElement.Text -> this
        is DataElement.Decimal -> this
        is DataElement.Array -> normalize()
        is DataElement.Struct -> normalize()
    }
}

private fun DataElement.Array.normalize(): DataElement? {
    val builder = DataElement.Array.Builder()
    forEach { item ->
        val normalizedItem = item.normalize()
        if (normalizedItem != null) builder.add(normalizedItem)
    }
    return if (builder.hasItems) builder.build() else null
}

private fun DataElement.Struct.normalize(): DataElement? {
    val builder = DataElement.Struct.Builder()
    forEach { (key, value) ->
        val normalizedValue = value.normalize()
        if (normalizedValue != null) builder[key] = normalizedValue
    }
    return if (builder.hasProperties) builder.build() else null
}
