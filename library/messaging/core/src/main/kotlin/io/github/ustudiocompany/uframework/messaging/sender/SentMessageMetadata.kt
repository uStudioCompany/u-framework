package io.github.ustudiocompany.uframework.messaging.sender

public interface SentMessageMetadata {
    public val offset: Long
    public val hasTimestamp: Boolean
    public val timestamp: Long
    public val topic: String
    public val partition: Int
}
