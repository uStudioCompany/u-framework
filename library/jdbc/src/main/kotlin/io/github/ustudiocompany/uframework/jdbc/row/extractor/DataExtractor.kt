package io.github.ustudiocompany.uframework.jdbc.row.extractor

import java.sql.ResultSet

public typealias DataExtractor<ValueT> = (index: Int, rs: ResultSet) -> ValueT?
