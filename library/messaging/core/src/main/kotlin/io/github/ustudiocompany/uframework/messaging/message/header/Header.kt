package io.github.ustudiocompany.uframework.messaging.message.header

import kotlin.text.Charsets.UTF_8

public class Header(
    public val name: String,
    private val value: ByteArray
) {
    public fun valueAsByteArray(): ByteArray = value

    public fun valueAsString(): String = String(value, UTF_8)

    public fun equals(name: String, ignoreCase: Boolean): Boolean = this.name.equals(name, ignoreCase)

    public companion object;
}
