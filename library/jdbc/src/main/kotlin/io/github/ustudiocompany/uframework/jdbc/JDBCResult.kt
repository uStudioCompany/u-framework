package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError

public typealias JDBCResult<T> = ResultK<T, TransactionError>
