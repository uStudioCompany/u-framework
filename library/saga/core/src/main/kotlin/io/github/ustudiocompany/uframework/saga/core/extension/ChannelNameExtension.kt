package io.github.ustudiocompany.uframework.saga.core.extension

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.messaging.message.ChannelName

public fun String.toChannelName(): ChannelName = ChannelName.of(this).orThrow { it.toIllegalArgumentException() }
