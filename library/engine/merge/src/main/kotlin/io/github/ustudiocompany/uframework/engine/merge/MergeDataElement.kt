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
        else -> MergeError.Source.TypeMismatch(path = path, expected = "Bool", actual = src.getTypeName())
            .asFailure()
    }

private fun mergeText(src: DataElement, path: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Text -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = path, expected = "Text", actual = src.getTypeName())
            .asFailure()
    }

private fun mergeDecimal(src: DataElement, path: AttributePath): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Decimal -> src.asSuccess()
        else -> MergeError.Source.TypeMismatch(path = path, expected = "Decimal", actual = src.getTypeName())
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
        else -> MergeError.Source.TypeMismatch(path = path, expected = "Array", actual = src.getTypeName())
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
        else -> MergeError.Source.TypeMismatch(
            path = path,
            expected = "Struct",
            actual = src.getTypeName()
        ).asFailure()
    }

private fun mergeStruct(
    dst: DataElement.Struct,
    src: DataElement.Struct,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement?, MergeError> {
    val properties = mutableMapOf<String, DataElement>()

    //Update
    dst.forEach { (key, oldValue) ->
        val newValue = src[key]
        if (newValue != null)
            merge(dst = oldValue, src = newValue, strategy = strategy, path = path.append(key))
                .getOrForward { return it }
                ?.let { mergedValue -> properties.put(key, mergedValue) }
        else
            oldValue
    }

    //Append
    src.forEach { (key, value) ->
        if (key !in dst)
            value.normalize()?.let { properties.put(key, it) }
    }

    return if (properties.isNotEmpty())
        DataElement.Struct(properties).asSuccess()
    else
        ResultK.Success.asNull
}

private fun wholeListMerge(src: DataElement): ResultK<DataElement?, MergeError> =
    src.normalize()?.asSuccess() ?: ResultK.Success.asNull

private fun mergeByAttributes(
    attributes: List<String>,
    dst: DataElement.Array,
    src: DataElement.Array,
    strategy: MergeStrategy,
    path: AttributePath
): ResultK<DataElement.Array, MergeError> {
    val srcItems = src.map { item ->
        val struct = item as? DataElement.Struct
            ?: return MergeError.Source.TypeMismatch(
                path = path,
                expected = "Struct",
                actual = item.getTypeName()
            ).asFailure()

        val id: ID = attributes.map { attribute ->
            struct[attribute]
                ?: return MergeError.Source.AttributeForMergeStrategyMissing(path.append(attribute)).asFailure()
        }

        id to struct
    }.toMap()

    val resultItems = mutableListOf<DataElement>()

    //Update
    val itemIds: List<ID> = dst.map { item ->
        val dstValue = item as DataElement.Struct

        val id: ID = attributes.map { attribute ->
            dstValue[attribute]
                ?: return MergeError.Destination.AttributeForMergeStrategyMissing(path.append(attribute))
                    .asFailure()
        }

        val srcValue = srcItems[id]
        val mergedValue = if (srcValue != null)
            merge(dst = dstValue, src = srcValue, strategy = strategy, path = path)
                .getOrForward { return it }
        else
            item

        if (mergedValue != null) resultItems.add(mergedValue)

        id
    }

    //Append
    srcItems.forEach { (id, struct) ->
        if (id !in itemIds)
            struct.normalize()?.let { resultItems.add(it) }
    }

    return DataElement.Array(resultItems).asSuccess()
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

private fun DataElement.Array.normalize(): DataElement? =
    mutableListOf<DataElement>()
        .apply {
            val items = this@normalize
            items.forEach { item ->
                val normalizedItem = item.normalize()
                if (normalizedItem != null) add(normalizedItem)
            }
        }
        .let {
            if (it.isNotEmpty()) DataElement.Array(it) else null
        }

private fun DataElement.Struct.normalize(): DataElement? =
    mutableMapOf<String, DataElement>()
        .apply {
            val properties = this@normalize
            properties.forEach { (key, value) ->
                val normalizedValue = value.normalize()
                if (normalizedValue != null) put(key, normalizedValue)
            }
        }
        .let { properties ->
            if (properties.isNotEmpty()) DataElement.Struct(properties) else null
        }

private fun DataElement.getTypeName(): String = this::class.simpleName!!
