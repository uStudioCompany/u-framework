package io.github.ustudiocompany.uframework.jdbc.row

import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public typealias ResultRowMapper<ValueT, ErrorT> = (index: Int, row: ResultRow) -> TransactionResult<ValueT, ErrorT>
