package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.filterNotNull
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public inline fun <ValueT> ResultRow.ensureColumnValueNotNull(
    column: Int,
    extractor: ResultRow.(Int) -> JDBCResult<ValueT>
): JDBCResult<ValueT & Any> =
    extractor(column)
        .filterNotNull { JDBCError("The value of the column with index '$column' is null.") }
