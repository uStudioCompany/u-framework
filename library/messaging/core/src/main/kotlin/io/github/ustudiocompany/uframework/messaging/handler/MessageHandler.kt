package io.github.ustudiocompany.uframework.messaging.handler

import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage

public fun interface MessageHandler<T> {

    context (Logging, DiagnosticContext)
    public fun handle(message: IncomingMessage<T>)
}
