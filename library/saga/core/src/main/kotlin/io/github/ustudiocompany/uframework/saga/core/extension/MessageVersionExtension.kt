package io.github.ustudiocompany.uframework.saga.core.extension

import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion

public fun String.toMessageVersion(): MessageVersion =
    MessageVersion.of(this).orThrow { it.toIllegalArgumentException() }
