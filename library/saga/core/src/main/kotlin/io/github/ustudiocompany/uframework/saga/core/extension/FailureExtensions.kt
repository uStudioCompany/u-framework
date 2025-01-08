package io.github.ustudiocompany.uframework.saga.core.extension

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.exceptionOrNull
import io.github.ustudiocompany.uframework.failure.fullDescription

public fun Failure.toIllegalArgumentException(): IllegalArgumentException =
    IllegalArgumentException(fullDescription(), exceptionOrNull())
