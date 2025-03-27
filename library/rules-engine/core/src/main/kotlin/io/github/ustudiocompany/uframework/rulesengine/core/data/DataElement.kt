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

    public data class Array(
        private val items: MutableList<DataElement> = mutableListOf()
    ) : DataElement, MutableList<DataElement> by items {

        override fun toJson(): String = buildString {
            append("[")
            items.forEachIndexed { index, item ->
                if (index > 0) append(", ")
                append(item.toJson())
            }
            append("]")
        }
    }

    public data class Struct(
        private val properties: MutableMap<String, DataElement> = mutableMapOf()
    ) : DataElement, MutableMap<String, DataElement> by properties {

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
    }
}
