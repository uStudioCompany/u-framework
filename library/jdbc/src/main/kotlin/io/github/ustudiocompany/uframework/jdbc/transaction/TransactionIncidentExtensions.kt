package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.either.Right
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

@PublishedApi
internal fun asIncident(description: String, exception: Throwable? = null): TransactionIncident =
    JDBCError(description, exception).asIncident()

@PublishedApi
internal fun JDBCError.asIncident(): TransactionIncident = Right(this).asFailure()
