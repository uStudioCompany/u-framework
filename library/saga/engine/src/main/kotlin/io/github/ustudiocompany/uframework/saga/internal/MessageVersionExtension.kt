package io.github.ustudiocompany.uframework.saga.internal

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageVersion

internal fun String.toMessageVersion() = MessageVersion.of(this).orThrow { it.toIllegalArgumentException() }
