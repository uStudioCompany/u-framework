package io.github.ustudiocompany.uframework.jdbc

import io.github.ustudiocompany.uframework.jdbc.exception.toTransactionError

internal inline fun <T> generalExceptionHandling(block: () -> JDBCResult<T>): JDBCResult<T> =
    try {
        block()
    } catch (expected: Exception) {
        expected.toTransactionError()
    }
