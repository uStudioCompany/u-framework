package io.github.ustudiocompany.uframework.jdbc

import io.github.ustudiocompany.uframework.jdbc.exception.toFailure

internal inline fun <T> generalExceptionHandling(block: () -> JDBCResult<T>): JDBCResult<T> =
    try {
        block()
    } catch (expected: Exception) {
        expected.toFailure()
    }
