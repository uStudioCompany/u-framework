package io.github.ustudiocompany.uframework.saga.request

import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.saga.MetaData
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageName
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageVersion

public class Request(
    public val channel: ChannelName,
    public val metadata: MetaData,
    public val name: MessageName,
    public val version: MessageVersion,
    public val body: String?
)
