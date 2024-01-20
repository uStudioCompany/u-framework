package io.github.ustudiocompany.uframework.messaging.message.header

public class Headers private constructor(private val items: List<Header>) : Iterable<Header> {

    public fun add(header: Pair<String, String>): Headers =
        add(Header(name = header.first, value = header.second.toByteArray()))

    public fun add(header: Header): Headers = Headers(this.items + header)

    public fun addAll(headers: Iterable<Header>): Headers = Headers(this.items + headers)

    public operator fun get(name: String, ignoreCase: Boolean = false): Iterable<Header> =
        Iterable { FilterByNameIterator(name = name, ignoreCase = ignoreCase, original = items.iterator()) }

    public fun last(name: String, ignoreCase: Boolean = false): Header? =
        items.findLast { header -> header.equals(name, ignoreCase) }

    public fun remove(name: String, ignoreCase: Boolean = false): Headers =
        Headers(items.filterNot { header -> header.equals(name, ignoreCase) })

    override fun iterator(): Iterator<Header> = items.iterator()

    public companion object {

        public val Empty: Headers = Headers(emptyList())

        @JvmStatic
        public operator fun invoke(vararg headers: Header): Headers = invoke(headers.toList())

        @JvmStatic
        public operator fun invoke(headers: List<Header>): Headers =
            if (headers.isNotEmpty()) Headers(headers) else Empty
    }

    private class FilterByNameIterator(
        val name: String,
        val ignoreCase: Boolean,
        val original: Iterator<Header>
    ) : AbstractIterator<Header>() {
        override fun computeNext() {
            while (true) {
                if (original.hasNext()) {
                    val header: Header = original.next()
                    if (!header.equals(name, ignoreCase)) continue
                    setNext(header)
                } else
                    done()
                return
            }
        }
    }
}
