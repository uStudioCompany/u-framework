package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError

public class Context private constructor(private val data: MutableMap<Source, DataElement>) {

    public fun add(source: Source, value: DataElement): ResultK<Unit, ContextError> =
        if (source.isNotPresent()) {
            data[source] = value
            Success.asUnit
        } else
            ContextError.SourceAlreadyExists(source).asFailure()

    public fun replace(source: Source, value: DataElement): ResultK<Unit, ContextError> =
        if (source.isPresent()) {
            data[source] = value
            Success.asUnit
        } else
            ContextError.SourceMissing(source).asFailure()

    public operator fun get(source: Source): ResultK<DataElement, ContextError> {
        val origin = data[source]
        return if (origin != null)
            origin.asSuccess()
        else
            ContextError.SourceMissing(source).asFailure()
    }

    public fun merge(
        source: Source,
        value: DataElement,
        merge: (origin: DataElement, target: DataElement) -> ResultK<DataElement, Failure>
    ): ResultK<Unit, ContextError> {
        val origin = data[source] ?: return ContextError.SourceMissing(source).asFailure()
        val newValue = merge(origin, value).getOrForward { return ContextError.Merge(source, it.cause).asFailure() }
        data[source] = newValue
        return Success.asUnit
    }

    public operator fun contains(source: Source): Boolean = source in data

    private fun Source.isPresent(): Boolean = this in data
    private fun Source.isNotPresent(): Boolean = this !in data

    public companion object {
        public fun empty(): Context = Context(mutableMapOf<Source, DataElement>())
        public operator fun invoke(elements: Map<Source, DataElement>): Context = Context(elements.toMutableMap())
    }
}
