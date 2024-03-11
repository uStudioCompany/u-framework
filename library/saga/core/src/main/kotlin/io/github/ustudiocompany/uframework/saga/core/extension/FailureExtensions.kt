package io.github.ustudiocompany.uframework.saga.core.extension

import io.github.ustudiocompany.uframework.failure.Failure

public fun Failure.toIllegalArgumentException(): IllegalArgumentException =
    IllegalArgumentException(joinDescriptions(), getException())
