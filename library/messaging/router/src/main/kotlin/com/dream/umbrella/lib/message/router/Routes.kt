package com.dream.umbrella.lib.message.router

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.mapError
import io.github.airflux.functional.success
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

public class Routes<T, HANDLER>(private val items: Map<RouteSelector, Route<HANDLER>>) {

    context(Logging, DiagnosticContext)
    public fun match(message: IncomingMessage<T>): Result<Route<HANDLER>, RoutingErrors> {
        logger.debug { "A message routing..." }
        val selector = selector(message).getOrForward { return it }
        return findRoute(selector)
    }

    context(Logging, DiagnosticContext)
    private fun selector(message: IncomingMessage<T>): Result<RouteSelector, RoutingErrors> {
        logger.debug { "Extracting selector from headers of a message..." }
        val name = message.name().getOrForward { return it }
        val version = message.version().getOrForward { return it }
        return RouteSelector(name, version).success()
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<T>.name(): Result<MessageName, RoutingErrors> {
        val header = getHeader(MESSAGE_NAME_HEADER_NAME)
            ?: return RoutingErrors.MessageNameHeader.Missing.error()
        return MessageName.of(header.valueAsString())
            .mapError { failure -> RoutingErrors.MessageNameHeader.InvalidValue(failure) }
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<T>.version(): Result<MessageVersion, RoutingErrors> {
        val header = getHeader(MESSAGE_VERSION_HEADER_NAME)
            ?: return RoutingErrors.MessageVersionHeader.Missing.error()
        return MessageVersion.of(header.valueAsString())
            .mapError { failure -> RoutingErrors.MessageVersionHeader.InvalidValue(failure) }
    }

    context(Logging, DiagnosticContext)
    private fun findRoute(selector: RouteSelector): Result<Route<HANDLER>, RoutingErrors> {
        logger.debug { "Finding a message handler by selector ($selector)..." }
        return items[selector]
            ?.success()
            ?: RoutingErrors.RouteNotFound(selector).error()
    }

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<T>.getHeader(name: String): Header? {
        logger.debug { "Extracting the $name from the headers of a message..." }
        return headers.last(name)
    }

    public class Builder<T, HANDLER> {
        private val items = TreeMap<RouteSelector, Route<HANDLER>>()

        public fun add(selector: RouteSelector, handler: HANDLER): Boolean {
            if (selector in items) return false
            items[selector] = Route(
                name = selector.name,
                version = selector.version,
                handler = handler
            )
            return true
        }

        public fun build(): Routes<T, HANDLER> = Routes<T, HANDLER>(items)
    }
}
