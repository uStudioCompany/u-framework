package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public typealias RowMapper<T, E> = (index: Int, row: ResultRow) -> TransactionResult<T, E>
