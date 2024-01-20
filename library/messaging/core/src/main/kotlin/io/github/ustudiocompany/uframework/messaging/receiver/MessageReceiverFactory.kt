package io.github.ustudiocompany.uframework.messaging.receiver

public fun interface MessageReceiverFactory<out T> {
    public fun create(): MessageReceiver<T>
}
