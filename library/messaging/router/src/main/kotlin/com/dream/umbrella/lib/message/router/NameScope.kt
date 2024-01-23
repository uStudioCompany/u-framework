package com.dream.umbrella.lib.message.router

import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler

public class NameScope<T> internal constructor(
    private val name: String,
    private val messageRoutingBuilder: RoutesScope<T>
) {

    private val handlersByVersions = mutableMapOf<String, MessageHandler<T>>()

    public fun version(version: String, handler: MessageHandler<T>) {
        handlersByVersions[version] = handler
    }

    public fun versions(vararg versions: String, handler: MessageHandler<T>): Unit =
        versions.forEach { version -> version(version, handler) }

    internal fun build(): Unit =
        handlersByVersions.forEach { (version, handler) ->
            messageRoutingBuilder.route(name, version, handler)
        }
}
