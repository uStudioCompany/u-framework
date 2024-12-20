package io.github.ustudiocompany.uframework.jdbc

import java.sql.Connection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
@Suppress("TooGenericExceptionCaught")
public inline fun <T> Connection.withTransaction(block: Connection.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        autoCommit = false
        val result = block(this)
        commit()
        result
    } catch (commitException: Throwable) {
        try {
            rollback()
        } catch (rollbackException: Throwable) {
            commitException.addSuppressed(rollbackException)
        }
        throw commitException
    }
}
