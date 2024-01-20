package io.github.ustudiocompany.uframework.messaging.receiver

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessages

public interface MessageReceiver<out T> : AutoCloseable {
    public fun subscribe(topics: Collection<String>)
    public fun poll(): IncomingMessages<T>
    public fun commitAsync()
    public fun commitSync()
}
