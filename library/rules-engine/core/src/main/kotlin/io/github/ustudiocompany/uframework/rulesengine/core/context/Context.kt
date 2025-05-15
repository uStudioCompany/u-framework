package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public class Context private constructor(private val data: MutableMap<Source, JsonElement>) {

    public val toMap: Map<Source, JsonElement>
        get() = data

    public operator fun get(source: Source): JsonElement? = data[source]

    public fun add(source: Source, value: JsonElement): Boolean =
        data.putIfAbsent(source, value) == null

    public fun replace(source: Source, value: JsonElement): Boolean =
        data.replace(source, value) != null

    public operator fun contains(source: Source): Boolean = source in data

    public companion object {
        public fun empty(): Context = Context(mutableMapOf<Source, JsonElement>())
        public operator fun invoke(elements: Map<Source, JsonElement>): Context = Context(elements.toMutableMap())
    }
}
