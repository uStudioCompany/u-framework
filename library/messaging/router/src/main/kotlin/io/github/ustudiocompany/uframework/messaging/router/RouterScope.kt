package io.github.ustudiocompany.uframework.messaging.router

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.debug
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

context(Logging, DiagnosticContext)
public class RouterScope<T, HANDLER> {
    private val router = Router.Builder<T, HANDLER>()

    public fun versions(vararg values: String): List<String> = values.asList()

    public fun route(name: String, version: String, handler: HANDLER): Unit =
        register(name = name.toMessageName(), version = version.toMessageVersion(), handler = handler)

    public fun route(name: String, versions: Collection<String>, handler: HANDLER): Unit =
        versions.forEach { version -> route(name, version, handler) }

    public fun route(name: String, block: NameScope<T, HANDLER>.() -> Unit): Unit =
        NameScope(name = name, this).apply(block).build()

    public fun build(): Router<T, HANDLER> = router.build()

    private fun String.toMessageName(): MessageName = MessageName.of(this)
        .orThrow { failure -> IllegalArgumentException("Invalid name. ${failure.joinDescriptions()}") }

    private fun String.toMessageVersion(): MessageVersion = MessageVersion.of(this)
        .orThrow { failure -> IllegalArgumentException("Invalid version. ${failure.joinDescriptions()}") }

    private fun register(
        name: MessageName,
        version: MessageVersion,
        handler: HANDLER
    ) {
        val selector = RouteSelector(name, version)
        val added: Boolean = router.add(selector, handler)
        check(added) { "A route by selector ($selector) is already registered.." }
        logger.debug { "A route by selector ($selector) was registered" }
    }
}