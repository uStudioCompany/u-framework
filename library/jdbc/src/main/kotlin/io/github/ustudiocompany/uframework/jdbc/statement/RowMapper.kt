package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public typealias RowMapper<ValueT, ErrorT> = (index: Int, row: ResultRow) -> TransactionResult<ValueT, ErrorT>
