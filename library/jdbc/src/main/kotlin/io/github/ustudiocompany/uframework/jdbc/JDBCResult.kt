package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public typealias JDBCResult<T> = ResultK<T, JDBCError>

public fun <T> jdbcError(description: String, exception: Throwable? = null): ResultK<T, JDBCError> =
    JDBCError(description, exception).asFailure()
