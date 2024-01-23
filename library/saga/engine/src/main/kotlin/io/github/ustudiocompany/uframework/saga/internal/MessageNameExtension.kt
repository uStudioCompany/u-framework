package io.github.ustudiocompany.uframework.saga.internal

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName

internal fun String.toMessageName() = MessageName.of(this).orThrow { it.toIllegalArgumentException() }
