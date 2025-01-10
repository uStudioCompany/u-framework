package io.github.ustudiocompany.uframework.jdbc.row.extractor

import java.sql.ResultSet

public typealias DataExtractor<T> = (index: Int, rs: ResultSet) -> T
