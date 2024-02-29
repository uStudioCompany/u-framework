@file:JvmName("DispatcherKt")

package io.github.ustudiocompany.uframework.messaging.dispatcher

import io.github.airflux.functional.getOrForward
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.DeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.channel.deadletter.sendToDeadLetterChannel
import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.router.Router
import io.github.ustudiocompany.uframework.messaging.router.RouterScope
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.error
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext

context(Logging, DiagnosticContext)
public fun <T> dispatcher(
    deadLetterChannel: DeadLetterChannel<T>? = null,
    router: RouterScope<T, MessageHandler<T>>.() -> Unit
): Dispatcher<T> = Dispatcher(
    deadLetterChannel = deadLetterChannel,
    router = RouterScope<T, MessageHandler<T>>().apply(router).build()
)

public class Dispatcher<T>(
    private val deadLetterChannel: DeadLetterChannel<T>?,
    private val router: Router<T, MessageHandler<T>>
) : MessageHandler<T> {

    context(Logging, DiagnosticContext)
    override fun handle(message: IncomingMessage<T>) {
        val route = router.match(message)
            .getOrForward { (failure) -> return handleFailure(failure, message) }
        route.handler.handle(message)
    }

    context(Logging, DiagnosticContext)
    private fun handleFailure(failure: Failure, message: IncomingMessage<T>) {
        if (deadLetterChannel != null)
            message.sendToDeadLetterChannel(deadLetterChannel, ERROR_DESCRIPTOR, failure)
        else
            failure.logging()
    }

    context(Logging, DiagnosticContext)
    private fun <F : Failure> F.logging() {
        withDiagnosticContext(this) {
            logger.error(getException()) { "$ERROR_DESCRIPTOR. ${joinDescriptions()}" }
        }
    }

    private companion object {
        private const val ERROR_DESCRIPTOR = "An error of a message router."
    }
}
