package io.github.ustudiocompany.uframework.saga.core.extension

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName

public fun String.toMessageName(): MessageName = MessageName.of(this).orThrow { it.toIllegalArgumentException() }
