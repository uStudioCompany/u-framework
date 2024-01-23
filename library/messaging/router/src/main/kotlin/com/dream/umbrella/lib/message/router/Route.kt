package com.dream.umbrella.lib.message.router

import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion

public class Route<T>(
    public val name: MessageName,
    public val version: MessageVersion,
    public val handler: MessageHandler<T>
)
