package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public typealias JDBCResult<T> = ResultK<T, JDBCError>

public fun jdbcError(description: String, exception: Throwable? = null): JDBCResult<Nothing> =
    JDBCError(description, exception).asFailure()
