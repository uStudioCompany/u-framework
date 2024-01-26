package io.github.ustudiocompany.uframework.messaging.router

public class NameScope<T, HANDLER> internal constructor(
    private val name: String,
    private val messageRoutingBuilder: RoutesScope<T, HANDLER>
) {

    private val handlersByVersions = mutableMapOf<String, HANDLER>()

    public fun version(version: String, handler: HANDLER) {
        handlersByVersions[version] = handler
    }

    public fun versions(vararg versions: String, handler: HANDLER): Unit =
        versions.forEach { version -> version(version, handler) }

    public fun build(): Unit =
        handlersByVersions.forEach { (version, handler) ->
            messageRoutingBuilder.route(name, version, handler)
        }
}
