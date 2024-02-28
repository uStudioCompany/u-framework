package io.github.ustudiocompany.uframework.jdbc

import java.sql.Connection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <T> Connection.withTransaction(block: Connection.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val exception: Throwable?
    return try {
        autoCommit = false
        val result = block(this)
        commit()
        result
    } catch (expected: Exception) {
        exception = expected
        try {
            rollback()
        } catch (ignore: Exception) {
        }
        throw exception
    }
}
