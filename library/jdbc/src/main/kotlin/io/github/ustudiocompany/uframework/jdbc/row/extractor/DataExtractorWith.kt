package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import java.sql.ResultSet

public typealias DataExtractorWith<T> = (index: Int, rs: ResultSet) -> JDBCResult<T>
