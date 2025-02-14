package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public typealias JDBCResult<ValueT> = ResultK<ValueT, JDBCError>
