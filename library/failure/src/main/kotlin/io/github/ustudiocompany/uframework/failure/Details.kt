package io.github.ustudiocompany.uframework.failure

/**
 * This type representing a collection of key-value pairs, called details.
 * The order of items in the details is not guaranteed.
 */
public class Details private constructor(private val items: List<Item>) : Iterable<Details.Item> by items {

    /**
     * Returns the value of the item from details by the specified [key] or null if the item is not found.
     * @param key the key for search.
     * @return the value or null.
     */
    public operator fun get(key: String): String? = items.find { it.key == key }?.value

    /**
     * Concatenates two details.
     * @param details the details to concatenate.
     * @return the new details.
     */
    public operator fun plus(details: Details): Details = Details(this.items + details)

    /**
     * Adds the specified [item] to the details.
     * @param item the item to add.
     * @return the new details.
     */
    public operator fun plus(item: Item): Details = Details(this.items + item)

    override fun toString(): String = items.joinToString(prefix = "[", postfix = "]") { it.toString() }

    public data class Item(public val key: String, public val value: String) {
        override fun toString(): String = "$key=`$value`"
    }

    public companion object {

        /**
         * The empty details.
         */
        public val NONE: Details = Details(emptyList())

        /**
         * Creates a new details with the specified [items].
         * @param items the items to create a new details.
         * @return the new details.
         */
        public fun of(vararg items: Item): Details = of(items.toList())

        /**
         * Creates a new details with the specified [items].
         * @param items the key-value pairs to create a new details.
         * @return the new details.
         */
        public fun of(vararg items: Pair<String, String>): Details =
            of(items.map { Item(key = it.first, value = it.second) })

        /**
         * Creates a new details with the specified [items].
         * @param items the items to create a new details.
         * @return the new details.
         */
        public fun of(items: List<Item>): Details = if (items.isNotEmpty()) Details(items) else NONE
    }
}
