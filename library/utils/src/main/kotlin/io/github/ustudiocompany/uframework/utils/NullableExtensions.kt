package io.github.ustudiocompany.uframework.utils

import kotlin.contracts.ExperimentalContracts
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
