package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.github.ustudiocompany.uframework.retry.retry

public inline fun <T, F : Any> retry(scope: RetryScope, block: () -> ResultK<T, F>): ResultK<T, F> =
    retry(scope, ::isRecoverableError, block)

public fun <F> isRecoverableError(failure: F): Boolean = failure is JDBCErrors.Connection
