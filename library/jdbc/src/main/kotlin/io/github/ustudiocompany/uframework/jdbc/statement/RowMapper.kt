package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow

public typealias RowMapper<T> = (index: Int, row: ResultRow) -> JDBCResult<T>
