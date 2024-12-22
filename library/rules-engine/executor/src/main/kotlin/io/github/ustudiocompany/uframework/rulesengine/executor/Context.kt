package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.failure
import io.github.airflux.commons.types.resultk.success
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError

public class Context {

    private val data = mutableMapOf<Source, DataElement>()

    public fun insert(source: Source, value: DataElement): ResultK<Unit, ContextError> {
        if (source in data)
            return ContextError.SourceAlreadyExists(source).asFailure()
        else
            data[source] = value
        return Success.asUnit
    }

    public fun update(source: Source, value: DataElement): ResultK<Unit, ContextError> {
        if (source in data)
            return ContextError.SourceMissing(source).asFailure()
        else
            data[source] = value
        return Success.asUnit
    }

    public operator fun get(source: Source): ResultK<DataElement, ContextError> =
        data[source]?.let { success(it) }
            ?: failure(ContextError.SourceMissing(source))

    public operator fun contains(source: Source): Boolean = source in data
}
