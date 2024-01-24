package com.dream.umbrella.lib.message.router

import io.github.airflux.functional.getOrForward
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.api.error
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.logging.diagnostic.context.withDiagnosticContext
import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.publisher.DeadLetterChannel

context(Logging, DiagnosticContext)
public fun <T> router(
    deadLetterChannel: DeadLetterChannel<T>? = null,
    router: RoutesScope<T, MessageHandler<T>>.() -> Unit
): Router<T> =
    Router(routes = RoutesScope<T, MessageHandler<T>>().apply(router).build(), deadLetterChannel = deadLetterChannel)

public class Router<T>(
    private val routes: Routes<T, MessageHandler<T>>,
    private val deadLetterChannel: DeadLetterChannel<T>?
) : MessageHandler<T> {

    context(Logging, DiagnosticContext)
    override fun handle(message: IncomingMessage<T>) {
        val route = routes.match(message)
            .getOrForward { (failure) -> return handleFailure(failure, message) }
        route.handler.handle(message)
    }

    context(Logging, DiagnosticContext)
    private fun handleFailure(failure: Failure, message: IncomingMessage<T>) {
        val stamp = createDeadLetterStamp(message)
        failure.logging(DeadLetterChannel.Stamp.KEY to stamp.get)
        message.forwardToDeadLetterChannel(stamp)
    }

    context(Logging, DiagnosticContext)
    private fun createDeadLetterStamp(message: IncomingMessage<T>): DeadLetterChannel.Stamp =
        DeadLetterChannel.Stamp.generate(message)

    context(Logging, DiagnosticContext)
    private fun IncomingMessage<T>.forwardToDeadLetterChannel(stamp: DeadLetterChannel.Stamp) {
        deadLetterChannel?.send(this@forwardToDeadLetterChannel, stamp)
    }

    context(Logging, DiagnosticContext)
    private fun <F : Failure> F.logging(vararg details: Pair<String, Any>) {
        withDiagnosticContext(this, Iterable { details.iterator() }) {
            logger.error(getException()) { "An error of command message router. ${joinDescriptions()}" }
        }
    }
}
