package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.github.ustudiocompany.uframework.retry.retry

public inline fun <T> retry(
    scope: RetryScope,
    block: () -> Result<T, JDBCErrors>
): Result<T, JDBCErrors> =
    retry(scope, ::isRecoverableError, block)

public fun isRecoverableError(failure: JDBCErrors): Boolean = failure is JDBCErrors.Connection
