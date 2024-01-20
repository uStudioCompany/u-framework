package io.github.ustudiocompany.uframework.saga.internal

import io.github.ustudiocompany.uframework.failure.Failure

internal fun Failure.toIllegalArgumentException() = IllegalArgumentException(joinDescriptions(), getException())
