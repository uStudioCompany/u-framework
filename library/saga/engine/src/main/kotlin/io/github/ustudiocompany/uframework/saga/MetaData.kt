package io.github.ustudiocompany.uframework.saga

public class MetaData(public val values: Map<String, String>) {

    public fun and(key: String, value: String): MetaData = plus(key to value)

    public fun andIfNotPresent(key: String, value: String): MetaData =
        if (key in values) this else and(key, value)

    public operator fun plus(item: Pair<String, String>): MetaData = MetaData(values + item)

    public operator fun get(key: String): String? = values[key]

    public companion object {

        public val Empty: MetaData = MetaData(emptyMap())

        public fun from(items: Map<String, String>): MetaData =
            if (items.isEmpty()) Empty else MetaData(items)

        public fun from(vararg items: Pair<String, String>): MetaData = MetaData(items.toMap())

        public fun with(key: String, value: String): MetaData = MetaData(mapOf(key to value))
    }
}
