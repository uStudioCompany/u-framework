package io.github.ustudiocompany.uframework.messaging.router

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.getOrForward
import io.github.airflux.commons.types.result.mapFailure
import io.github.airflux.commons.types.result.orThrow
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_NAME_HEADER_NAME
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_VERSION_HEADER_NAME
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.debug
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import java.util.*

context(Logging, DiagnosticContext)
public fun <BODY, HANDLER> router(
    scope: RouterScope<BODY, HANDLER>.() -> Unit
): Router<BODY, HANDLER> = RouterScope<BODY, HANDLER>().apply(scope).build()

public class Router<BODY, HANDLER> internal constructor(private val items: Map<RouteSelector, Route<HANDLER>>) {

    context(Logging, DiagnosticContext)
    public fun match(message: IncomingMessage<BODY>): Result<Route<HANDLER>, RouterErrors> {
        logger.debug { "A message routing..." }
        val selector = selector(message).getOrForward { return it }
        return findRoute(selector)
    }

    context(Logging, DiagnosticContext)
    private fun selector(message: IncomingMessage<BODY>): Result<RouteSelector, RouterErrors> {
        logger.debug { "Extracting selector from headers of a message..." }
        val name = message.name().getOrForward { return it }
        val version = message.version().getOrForward { return it }
        return RouteSelector(name, version).success()
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<BODY>.name(): Result<MessageName, RouterErrors> {
        val header = getHeader(MESSAGE_NAME_HEADER_NAME)
            ?: return RouterErrors.MessageNameHeader.Missing.failure()
        return MessageName.of(header.valueAsString())
            .mapFailure { failure -> RouterErrors.MessageNameHeader.InvalidValue(failure) }
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<BODY>.version(): Result<MessageVersion, RouterErrors> {
        val header = getHeader(MESSAGE_VERSION_HEADER_NAME)
            ?: return RouterErrors.MessageVersionHeader.Missing.failure()
        return MessageVersion.of(header.valueAsString())
            .mapFailure { failure -> RouterErrors.MessageVersionHeader.InvalidValue(failure) }
    }

    context(Logging, DiagnosticContext)
    private fun findRoute(selector: RouteSelector): Result<Route<HANDLER>, RouterErrors> {
        logger.debug { "Finding a message handler by selector ($selector)..." }
        return items[selector]
            ?.success()
            ?: RouterErrors.RouteNotFound(selector).failure()
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<BODY>.getHeader(name: String): Header? {
        logger.debug { "Extracting the $name from the headers of a message..." }
        return headers.lastOrNull(name)
    }

    public class Builder<BODY, HANDLER> {
        private val items = TreeMap<RouteSelector, Route<HANDLER>>()

        public fun add(name: String, version: String, handler: HANDLER): Boolean =
            add(name = name.toMessageName(), version = version.toMessageVersion(), handler = handler)

        public fun add(name: MessageName, version: MessageVersion, handler: HANDLER): Boolean =
            add(RouteSelector(name, version), handler)

        public fun add(selector: RouteSelector, handler: HANDLER): Boolean {
            if (selector in items) return false
            items[selector] = Route(
                name = selector.name,
                version = selector.version,
                handler = handler
            )
            return true
        }

        public fun build(): Router<BODY, HANDLER> = Router(items)

        private fun String.toMessageName(): MessageName = MessageName.of(this)
            .orThrow { failure -> IllegalArgumentException("Invalid a message name. ${failure.joinDescriptions()}") }

        private fun String.toMessageVersion(): MessageVersion = MessageVersion.of(this)
            .orThrow { failure -> IllegalArgumentException("Invalid a message version. ${failure.joinDescriptions()}") }
    }
}
