package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError

public class Context private constructor(private val data: MutableMap<Source, DataElement>) {

    public fun add(source: Source, value: DataElement): ResultK<Unit, ContextError> =
        if (source in data)
            ContextError.SourceAlreadyExists(source).asFailure()
        else {
            data[source] = value
            Success.Companion.asUnit
        }

    public fun replace(source: Source, value: DataElement): ResultK<Unit, ContextError> =
        if (source in data) {
            data[source] = value
            Success.asUnit
        } else
            ContextError.SourceMissing(source).asFailure()

    public operator fun get(source: Source): ResultK<DataElement, ContextError> {
        val value = data[source]
        return if (value != null)
            value.asSuccess()
        else
            ContextError.SourceMissing(source).asFailure()
    }

    public operator fun contains(source: Source): Boolean = source in data

    public companion object {
        public fun empty(): Context = Context(mutableMapOf<Source, DataElement>())
        public operator fun invoke(elements: Map<Source, DataElement>): Context = Context(elements.toMutableMap())
    }
}
