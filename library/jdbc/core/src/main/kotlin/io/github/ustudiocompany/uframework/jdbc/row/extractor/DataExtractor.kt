package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import java.sql.ResultSet

public typealias DataExtractor<ValueT> = (index: Int, rs: ResultSet) -> JDBCResult<ValueT>
