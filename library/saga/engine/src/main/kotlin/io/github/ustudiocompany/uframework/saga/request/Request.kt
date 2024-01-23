package io.github.ustudiocompany.uframework.saga.request

import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.saga.MetaData

public class Request(
    public val channel: ChannelName,
    public val metadata: MetaData,
    public val name: MessageName,
    public val version: MessageVersion,
    public val body: String?
)
