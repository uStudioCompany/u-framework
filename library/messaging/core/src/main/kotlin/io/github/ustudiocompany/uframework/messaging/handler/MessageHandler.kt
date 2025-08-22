package io.github.ustudiocompany.uframework.messaging.handler

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage

public fun interface MessageHandler<T> {

    public fun handle(message: IncomingMessage<T>)
}
