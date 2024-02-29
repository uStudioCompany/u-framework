@file:JvmName("DispatcherKt")

package io.github.ustudiocompany.uframework.messaging.dispatcher

import io.github.airflux.functional.fold
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.DeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.sendToDeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.dispatcher.Dispatcher.FailureHandler
import io.github.ustudiocompany.uframework.messaging.dispatcher.Dispatcher.RouteHandler
import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.router.Route
import io.github.ustudiocompany.uframework.messaging.router.Router
import io.github.ustudiocompany.uframework.messaging.router.RouterScope
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.error
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext

context(Logging, DiagnosticContext)
public fun <T, H> dispatcher(
    routeHandler: RouteHandler<T, H>,
    failureHandler: FailureHandler<T>,
    router: RouterScope<T, H>.() -> Unit
): Dispatcher<T, H> =
    Dispatcher(
        routeHandler = routeHandler,
        failureHandler = failureHandler,
        router = RouterScope<T, H>().apply(router).build()
    )

public class Dispatcher<T, H>(
    private val router: Router<T, H>,
    private val routeHandler: RouteHandler<T, H>,
    private val failureHandler: FailureHandler<T>
) : MessageHandler<T> {

    context(Logging, DiagnosticContext)
    override fun handle(message: IncomingMessage<T>) {
        router.match(message)
            .fold(
                onSuccess = { route -> routeHandler.handle(message, route) },
                onError = { failure -> failureHandler.handle(message, failure) }
            )
    }

    public fun interface RouteHandler<T, H> {

        context(Logging, DiagnosticContext)
        public fun handle(message: IncomingMessage<T>, route: Route<H>)

        public companion object {
            public fun <T> defaultMessageHandler(): RouteHandler<T, MessageHandler<T>> =
                RouteHandler { message, route -> route.handler.handle(message) }
        }
    }

    public fun interface FailureHandler<T> {

        context(Logging, DiagnosticContext)
        public fun handle(message: IncomingMessage<T>, failure: Failure)

        public companion object {

            public val none: FailureHandler<Any> = FailureHandler { _, _ -> }

            public val onlyLogging: FailureHandler<Any> =
                FailureHandler { _, failure -> failure.logging() }

            public fun <T> deadLetter(deadLetterChannel: DeadLetterChannel<T>): FailureHandler<T> =
                FailureHandler { message, failure ->
                    message.sendToDeadLetterChannel(deadLetterChannel, ERROR_DESCRIPTOR, failure)
                }

            private const val ERROR_DESCRIPTOR = "An error of a message router."

            context(Logging, DiagnosticContext)
            private fun Failure.logging(description: String? = null) {
                withDiagnosticContext(this) {
                    logger.error(getException()) {
                        if (description != null)
                            "$ERROR_DESCRIPTOR $description ${joinDescriptions()}"
                        else
                            "$ERROR_DESCRIPTOR ${joinDescriptions()}"
                    }
                }
            }
        }
    }
}
