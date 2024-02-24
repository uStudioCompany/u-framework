package io.github.ustudiocompany.uframework.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun <T> T?.isPresent(): Boolean {
    contract {
        returns(true) implies (this@isPresent != null)
    }
    return this != null
}

@OptIn(ExperimentalContracts::class)
public fun <T> T?.isNotPresent(): Boolean {
    contract {
        returns(false) implies (this@isNotPresent != null)
    }
    return this == null
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> T?.notNull(ifNull: () -> Nothing): T {
    contract {
        callsInPlace(ifNull, InvocationKind.AT_MOST_ONCE)
        returns() implies (this@notNull != null)
    }
    return this ?: ifNull()
}
