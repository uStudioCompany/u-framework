package io.github.ustudiocompany.uframework.messaging.handler

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public fun interface MessageHandler<T> {

    context (Logging, DiagnosticContext)
    public fun handle(message: IncomingMessage<T>)
}
