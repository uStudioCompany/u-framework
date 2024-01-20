package io.github.ustudiocompany.uframework.saga.internal

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.messaging.message.ChannelName

internal fun String.toChannelName() = ChannelName.of(this).orThrow { it.toIllegalArgumentException() }
