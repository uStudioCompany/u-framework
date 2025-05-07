package io.github.ustudiocompany.uframework.rulesengine.core.data

import java.math.BigDecimal

public sealed interface DataElement {

    public fun toJson(): String

    public companion object;

    public data object Null : DataElement {
        override fun toJson(): String = "null"
    }

    public data class Bool(val get: Boolean) : DataElement {

        override fun toJson(): String = get.toString()

        public companion object {
            public val asTrue: Bool = Bool(true)
            public val asFalse: Bool = Bool(false)

            @JvmStatic
            public fun valueOf(value: Boolean): Bool = if (value) asTrue else asFalse
        }
    }

    public data class Text(val get: String) : DataElement {
        override fun toJson(): String = "\"$get\""
    }

    public data class Decimal(val get: BigDecimal) : DataElement {

        override fun equals(other: Any?): Boolean =
            this === other || other is Decimal && this.get.compareTo(other.get) == 0

        override fun hashCode(): Int = get.hashCode()

        override fun toJson(): String = get.toPlainString()
    }

    public class Array private constructor(
        private val items: MutableList<DataElement>
    ) : DataElement, MutableList<DataElement> by items {

        public constructor(vararg items: DataElement) : this(
            mutableListOf<DataElement>()
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

        public class Builder {
            private val items: MutableList<DataElement> = mutableListOf()

            public val hasItems: Boolean
                get() = items.isNotEmpty()

            public fun add(value: DataElement): Builder = apply {
                items.add(value)
            }

            public fun build(): Array = Array(items)
        }
    }

    public class Struct private constructor(
        private val properties: MutableMap<String, DataElement>
    ) : DataElement, MutableMap<String, DataElement> by properties {

        public constructor(vararg properties: Pair<String, DataElement>) : this(
            mutableMapOf<String, DataElement>()
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

        public class Builder {
            private val properties = mutableMapOf<String, DataElement>()

            public val hasProperties: Boolean
                get() = properties.isNotEmpty()

            public operator fun set(name: String, value: DataElement): Builder = apply {
                properties.put(name, value)
            }

            public fun build(): Struct = Struct(properties)
        }
    }
}
