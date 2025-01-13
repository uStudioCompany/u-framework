package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public typealias TransactionError = ResultK<Nothing, Either<Nothing, JDBCError>>
