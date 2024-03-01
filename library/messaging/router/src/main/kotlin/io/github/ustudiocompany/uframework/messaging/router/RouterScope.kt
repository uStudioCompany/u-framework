package io.github.ustudiocompany.uframework.messaging.router

import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.debug
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

context(Logging, DiagnosticContext)
public class RouterScope<BODY, HANDLER> {
    private val router = Router.Builder<BODY, HANDLER>()

    public fun versions(vararg values: String): List<String> = values.asList()

    public fun route(name: String, version: String, handler: HANDLER): Unit =
        register(name = name, version = version, handler = handler)

    public fun route(name: String, versions: Collection<String>, handler: HANDLER): Unit =
        versions.forEach { version -> route(name, version, handler) }

    public fun route(name: String, block: NameScope<BODY, HANDLER>.() -> Unit): Unit =
        NameScope(name = name, this).apply(block).build()

    public fun build(): Router<BODY, HANDLER> = router.build()

    private fun register(name: String, version: String, handler: HANDLER) {
        val added: Boolean = router.add(name, version, handler)
        check(added) { "A route by selector (name: `$name`, version `$version`) is already registered.." }
        logger.debug { "A route by selector (name: `$name`, version `$version`) was registered" }
    }
}
