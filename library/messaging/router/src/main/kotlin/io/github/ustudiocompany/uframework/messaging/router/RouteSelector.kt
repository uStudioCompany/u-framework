package io.github.ustudiocompany.uframework.messaging.router

import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion

public class RouteSelector(
    public val name: MessageName,
    public val version: MessageVersion
) : Comparable<RouteSelector> {

    override fun equals(other: Any?): Boolean =
        this === other || (other is RouteSelector && this.name == other.name && this.version == other.version)

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }

    override fun toString(): String = "name: `$name`, version: `$version`"

    override fun compareTo(other: RouteSelector): Int {
        val byName = name.compareTo(other.name)
        return if (byName != 0) byName else version.compareTo(other.version)
    }
}
