package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public class Context private constructor(
    private val data: MutableMap<Source, JsonElement>
) : Iterable<Pair<Source, JsonElement>> {

    public fun isEmpty(): Boolean = data.isEmpty()

    public fun isNotEmpty(): Boolean = !isEmpty()

    public fun getOrNull(source: Source): JsonElement? = data[source]

    public fun putIfAbsent(source: Source, value: JsonElement): Boolean =
        data.putIfAbsent(source, value) == null

    public fun putIfPresent(source: Source, value: JsonElement): Boolean =
        data.replace(source, value) != null

    public operator fun contains(source: Source): Boolean = source in data

    public override fun iterator(): Iterator<Pair<Source, JsonElement>> = ContextIterator(data)

    public companion object {
        public fun empty(): Context = Context(data = mutableMapOf())

        public operator fun invoke(vararg sources: Pair<Source, JsonElement>): Context =
            Context(data = sources.toMap(mutableMapOf()))

        public operator fun invoke(sources: Map<Source, JsonElement> = emptyMap()): Context =
            Context(data = sources.toMutableMap())
    }

    public class Builder {
        private val sources: MutableMap<Source, JsonElement> = mutableMapOf()

        public operator fun set(source: Source, value: JsonElement): Builder {
            sources[source] = value
            return this
        }

        public fun build(): Context = Context(sources)
    }

    private class ContextIterator(data: Map<Source, JsonElement>) : AbstractIterator<Pair<Source, JsonElement>>() {
        val original = data.iterator()
        override fun computeNext() {
            if (original.hasNext())
                setNext(original.next().toPair())
            else
                done()
        }
    }
}
