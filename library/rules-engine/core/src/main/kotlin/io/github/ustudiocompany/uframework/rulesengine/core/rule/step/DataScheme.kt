package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

public sealed interface DataScheme {

    public data class Struct(val properties: List<Property>) : DataScheme

    public data class Array(val items: List<Item>) : DataScheme

    public sealed interface Property {
        public data class Struct(val name: String, val properties: List<Property>) : Property
        public data class Array(val name: String, val items: List<Item>) : Property
        public data class Element(val name: String, val value: Value) : Property
    }

    public sealed interface Item {
        public data class Struct(val properties: List<Property>) : Item
        public data class Array(val items: List<Item>) : Item
        public data class Element(val value: Value) : Item
    }
}
