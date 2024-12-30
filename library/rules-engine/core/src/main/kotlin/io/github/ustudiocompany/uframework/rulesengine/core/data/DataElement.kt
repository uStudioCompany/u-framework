package io.github.ustudiocompany.uframework.rulesengine.core.data

import java.math.BigDecimal

public sealed interface DataElement {

    public data object Null : DataElement {
        override fun toString(): String = "null"
    }

    public data class Bool(val get: Boolean) : DataElement {
        override fun toString(): String = get.toString()
    }

    public data class Text(val get: String) : DataElement {
        override fun toString(): String = "\"$get\""
    }

    public data class Decimal(val get: BigDecimal) : DataElement {
        override fun toString(): String = get.toString()

        override fun equals(other: Any?): Boolean =
            this === other || other is Decimal && this.get.compareTo(other.get) == 0

        override fun hashCode(): Int = get.hashCode()
    }

    public data class Array(
        private val items: MutableList<DataElement> = mutableListOf()
    ) : DataElement, MutableList<DataElement> by items {
        override fun toString(): String = items.toString()
    }

    public data class Struct(
        private val properties: MutableMap<String, DataElement> = mutableMapOf()
    ) : DataElement, MutableMap<String, DataElement> by properties {
        override fun toString(): String =
            properties.map { (name, value) -> "\"$name\": $value" }
                .joinToString(prefix = "{", postfix = "}")
    }
}
