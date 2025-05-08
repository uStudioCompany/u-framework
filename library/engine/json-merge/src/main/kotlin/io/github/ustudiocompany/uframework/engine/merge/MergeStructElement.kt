package io.github.ustudiocompany.uframework.engine.merge

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun mergeStruct(
    dst: DataElement.Struct,
    src: DataElement,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<DataElement?, MergeError> =
    when (src) {
        is DataElement.Null -> ResultK.Success.asNull
        is DataElement.Struct -> mergeStruct(dst = dst, src = src, strategy = strategy, currentPath = currentPath)
        else -> MergeError.Source.TypeMismatch(path = currentPath, expected = DataElement.Struct::class, actual = src)
            .asFailure()
    }

private fun mergeStruct(
    dst: DataElement.Struct,
    src: DataElement.Struct,
    strategy: MergeStrategy,
    currentPath: AttributePath
): ResultK<DataElement?, MergeError> {
    val builder = DataElement.Struct.Builder()

    //Update
    dst.forEach { (key, oldValue) ->
        val newValue = src[key]
        if (newValue != null)
            merge(dst = oldValue, src = newValue, strategy = strategy, currentPath = currentPath.append(key))
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
