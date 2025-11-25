package io.github.ustudiocompany.uframework.json.element

import java.math.BigDecimal

public sealed interface JsonElement {

    public fun toJson(): String

    public companion object;

    public data object Null : JsonElement {
        override fun toJson(): String = "null"
        override fun toString(): String = "Null()"
    }

    public data class Bool(val get: Boolean) : JsonElement {

        override fun toJson(): String = get.toString()

        public companion object {
            public val asTrue: Bool = Bool(true)
            public val asFalse: Bool = Bool(false)

            @JvmStatic
            public fun valueOf(value: Boolean): Bool = if (value) asTrue else asFalse
        }
    }

    public data class Text(val get: String) : JsonElement {
        override fun toJson(): String = "\"$get\""
    }

    public data class Decimal(val get: BigDecimal) : JsonElement {

        override fun equals(other: Any?): Boolean =
            this === other || other is Decimal && this.get.compareTo(other.get) == 0

        override fun hashCode(): Int = get.hashCode()

        override fun toJson(): String = get.toPlainString()
    }

    public class Array private constructor(
        private val items: MutableList<JsonElement>
    ) : JsonElement, MutableList<JsonElement> by items {

        public constructor(vararg items: JsonElement) : this(
            mutableListOf<JsonElement>()
                .apply {
                    items.forEach { this.add(it) }
                }
        )

        override fun toJson(): String = buildString {
            append("[")
            items.forEachIndexed { index, item ->
                if (index > 0) append(", ")
                append(item.toJson())
            }
            append("]")
        }

        override fun hashCode(): Int = items.hashCode()

        override fun equals(other: Any?): Boolean =
            this === other || (other is Array && items == other.items)

        override fun toString(): String = "Array($items)"

        public class Builder {
            private val items: MutableList<JsonElement> = mutableListOf()

            public val hasItems: Boolean
                get() = items.isNotEmpty()

            public fun add(value: JsonElement): Builder = apply {
                items.add(value)
            }

            public fun build(): Array = if (items.isNotEmpty()) Array(items) else EMPTY

            private companion object {
                private val EMPTY = Array()
            }
        }
    }

    public class Struct private constructor(
        private val properties: MutableMap<String, JsonElement>
    ) : JsonElement, MutableMap<String, JsonElement> by properties {

        public constructor(vararg properties: Pair<String, JsonElement>) : this(
            mutableMapOf<String, JsonElement>()
                .apply {
                    properties.forEach { (name, value) ->
                        this[name] = value
                    }
                }
        )

        override fun toJson(): String = buildString {
            append("{")
            var addSeparator = false
            properties.forEach { (name, value) ->
                if (addSeparator)
                    append(", ")
                else
                    addSeparator = true

                append("\"")
                append(name)
                append("\"")
                append(": ")
                append(value.toJson())
            }
            append("}")
        }

        override fun hashCode(): Int = properties.hashCode()

        override fun equals(other: Any?): Boolean =
            this === other || (other is Struct && properties == other.properties)

        override fun toString(): String = "Struct($properties)"

        public class Builder {
            private val properties = mutableMapOf<String, JsonElement>()

            public val hasProperties: Boolean
                get() = properties.isNotEmpty()

            public operator fun set(name: String, value: JsonElement): Builder = apply {
                properties.put(name, value)
            }

            public fun build(): Struct = if (properties.isNotEmpty()) Struct(properties) else EMPTY

            private companion object {
                private val EMPTY = Struct()
            }
        }
    }
}
