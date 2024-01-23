package com.dream.umbrella.lib.message.router

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.api.debug
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion

context(Logging, DiagnosticContext)
public class RoutesScope<T> {
    private val routes = Routes.Builder<T>()

    public fun versions(vararg values: String): List<String> = values.asList()

    public fun route(name: String, version: String, handler: MessageHandler<T>): Unit =
        register(name = name.toMessageName(), version = version.toMessageVersion(), handler = handler)

    public fun route(name: String, versions: Collection<String>, handler: MessageHandler<T>): Unit =
        versions.forEach { version -> route(name, version, handler) }

    public fun route(name: String, block: NameScope<T>.() -> Unit): Unit =
        NameScope(name = name, this).apply(block).build()

    public fun build(): Routes<T> = routes.build()

    private fun String.toMessageName(): MessageName = MessageName.of(this)
        .orThrow { failure -> IllegalArgumentException("Invalid name. ${failure.joinDescriptions()}") }

    private fun String.toMessageVersion(): MessageVersion = MessageVersion.of(this)
        .orThrow { failure -> IllegalArgumentException("Invalid version. ${failure.joinDescriptions()}") }

    private fun register(
        name: MessageName,
        version: MessageVersion,
        handler: MessageHandler<T>
    ) {
        val selector = RouteSelector(name, version)
        val added: Boolean = routes.add(selector, handler)
        check(added) { "A route by selector ($selector) is already registered.." }
        logger.debug { "A route by selector ($selector) was registered" }
    }
}
