package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.github.ustudiocompany.uframework.retry.retry

public inline fun <T, F> retry(scope: RetryScope, block: () -> Result<T, F>): Result<T, F> =
    retry(scope, ::isRecoverableError, block)

public fun <F> isRecoverableError(failure: F): Boolean = failure is JDBCErrors.Connection
