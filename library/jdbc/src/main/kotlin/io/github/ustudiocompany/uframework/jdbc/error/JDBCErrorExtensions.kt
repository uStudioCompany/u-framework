package io.github.ustudiocompany.uframework.jdbc.error

import io.github.airflux.commons.types.either.right
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionIncident

public fun incident(description: String, exception: Throwable? = null): TransactionIncident =
    JDBCError(description, exception).asIncident()

public fun JDBCError.asIncident(): TransactionIncident = right(this).asFailure()
