package io.github.ustudiocompany.uframework.messaging.router

import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion

public class Route<HANDLER>(
    public val name: MessageName,
    public val version: MessageVersion,
    public val handler: HANDLER
)
