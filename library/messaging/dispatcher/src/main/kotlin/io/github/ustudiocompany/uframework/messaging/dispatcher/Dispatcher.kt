@file:JvmName("DispatcherKt")

package io.github.ustudiocompany.uframework.messaging.dispatcher

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.DeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.sendToDeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.dispatcher.Dispatcher.FailureHandler
import io.github.ustudiocompany.uframework.messaging.dispatcher.Dispatcher.ResponsePostHandler
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
public fun <BODY, HANDLER, RESPONSE> dispatcher(
    routeHandler: RouteHandler<BODY, HANDLER, RESPONSE>,
    responsePostHandler: ResponsePostHandler<BODY, HANDLER, RESPONSE>,
    failureHandler: FailureHandler<BODY>,
    router: RouterScope<BODY, HANDLER>.() -> Unit
): Dispatcher<BODY, HANDLER, RESPONSE> =
    Dispatcher(
        routeHandler = routeHandler,
        responsePostHandler = responsePostHandler,
        failureHandler = failureHandler,
        router = RouterScope<BODY, HANDLER>().apply(router).build()
    )

context(Logging, DiagnosticContext)
public fun <BODY, HANDLER, RESPONSE> dispatcher(
    routeHandler: RouteHandler<BODY, HANDLER, RESPONSE>,
    responsePostHandler: ResponsePostHandler<BODY, HANDLER, RESPONSE>,
    failureHandler: FailureHandler<BODY>,
    router: Router<BODY, HANDLER>
): Dispatcher<BODY, HANDLER, RESPONSE> =
    Dispatcher(
        router = router,
        routeHandler = routeHandler,
        responsePostHandler = responsePostHandler,
        failureHandler = failureHandler,
    )

public class Dispatcher<BODY, HANDLER, RESPONSE>(
    private val router: Router<BODY, HANDLER>,
    private val routeHandler: RouteHandler<BODY, HANDLER, RESPONSE>,
    private val responsePostHandler: ResponsePostHandler<BODY, HANDLER, RESPONSE>,
    private val failureHandler: FailureHandler<BODY>
) : MessageHandler<BODY> {

    context(Logging, DiagnosticContext)
    override fun handle(message: IncomingMessage<BODY>) {
        val route = router.match(message)
            .getOrForward { (failure) -> return failureHandler.handle(DispatcherErrors.Route(failure), message) }
        val response = routeHandler.handle(route, message)
            .getOrForward { (failure) -> return failureHandler.handle(DispatcherErrors.Handler(failure), message) }
        responsePostHandler.handle(route, message, response)
            .getOrForward { (failure) -> return failureHandler.handle(DispatcherErrors.PostHandler(failure), message) }
    }

    public fun interface RouteHandler<BODY, HANDLER, RESPONSE> {

        context(Logging, DiagnosticContext)
        public fun handle(route: Route<HANDLER>, message: IncomingMessage<BODY>): ResultK<RESPONSE, Failure>

        public companion object {

            context(Logging, DiagnosticContext)
            public fun <BODY> messageHandler(): RouteHandler<BODY, MessageHandler<BODY>, Unit> =
                RouteHandler { route, message ->
                    route.handler.handle(message)
                    Success.asUnit
                }
        }
    }

    public fun interface ResponsePostHandler<BODY, HANDLER, RESPONSE> {

        context(Logging, DiagnosticContext)
        public fun handle(
            route: Route<HANDLER>,
            message: IncomingMessage<BODY>,
            response: RESPONSE
        ): ResultK<Unit, Failure>

        public companion object {

            context(Logging, DiagnosticContext)
            public fun <BODY, HANDLER, RESPONSE> none(): ResponsePostHandler<BODY, HANDLER, RESPONSE> =
                ResponsePostHandler { _, _, _ -> Success.asUnit }
        }
    }

    public fun interface FailureHandler<BODY> {

        context(Logging, DiagnosticContext)
        public fun handle(failure: DispatcherErrors, message: IncomingMessage<BODY>)

        public companion object {
            private const val ERROR_DESCRIPTOR = "The message dispatching error."

            context(Logging, DiagnosticContext)
            public fun <BODY> none(): FailureHandler<BODY> = FailureHandler { _, _ -> }

            context(Logging, DiagnosticContext)
            public fun <BODY> onlyLogging(
                descriptionBuilder: ((failure: DispatcherErrors, message: IncomingMessage<BODY>) -> String)? = null
            ): FailureHandler<BODY> =
                FailureHandler { failure, message ->
                    if (descriptionBuilder != null)
                        failure.logging(descriptionBuilder(failure, message))
                    else
                        failure.logging()
                }

            context(Logging, DiagnosticContext)
            public fun <BODY> deadLetter(
                deadLetterChannel: DeadLetterChannel<BODY>,
                descriptionBuilder: ((failure: DispatcherErrors, message: IncomingMessage<BODY>) -> String)? = null
            ): FailureHandler<BODY> =
                FailureHandler { failure, message ->
                    val description = if (descriptionBuilder != null)
                        ERROR_DESCRIPTOR + descriptionBuilder(failure, message)
                    else
                        ERROR_DESCRIPTOR
                    message.sendToDeadLetterChannel(deadLetterChannel, description, failure)
                }

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
