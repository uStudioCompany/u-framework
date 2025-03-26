package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public class Context private constructor(private val data: MutableMap<Source, DataElement>) {

    public val immutable: Map<Source, DataElement>
        get() = data

    public operator fun get(source: Source): DataElement? = data[source]

    public fun add(source: Source, value: DataElement): Boolean =
        data.putIfAbsent(source, value) == null

    public fun replace(source: Source, value: DataElement): Boolean =
        data.replace(source, value) != null

    public operator fun contains(source: Source): Boolean = source in data

    public companion object {
        public fun empty(): Context = Context(mutableMapOf<Source, DataElement>())
        public operator fun invoke(elements: Map<Source, DataElement>): Context = Context(elements.toMutableMap())
    }
}
