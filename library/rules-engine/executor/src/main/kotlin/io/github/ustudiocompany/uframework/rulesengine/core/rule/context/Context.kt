package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.failure
import io.github.airflux.commons.types.resultk.success
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError

public class Context private constructor(private val data: MutableMap<Source, DataElement>) {

    public fun add(source: Source, value: DataElement): ResultK<Unit, ContextError> {
        if (source in data)
            return ContextError.SourceAlreadyExists(source).asFailure()
        else
            data[source] = value
        return Success.Companion.asUnit
    }

    public fun replace(source: Source, value: DataElement): ResultK<Unit, ContextError> {
        if (source in data)
            return ContextError.SourceMissing(source).asFailure()
        else
            data[source] = value
        return Success.Companion.asUnit
    }

    public operator fun get(source: Source): ResultK<DataElement, ContextError> =
        data[source]?.let { success(it) }
            ?: failure(ContextError.SourceMissing(source))

    public operator fun contains(source: Source): Boolean = source in data

    public companion object {
        public fun empty(): Context = Context(mutableMapOf<Source, DataElement>())
        public operator fun invoke(elements: Map<Source, DataElement>): Context = Context(elements.toMutableMap())
    }
}
