package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes

@Deprecated(message = "Use `getLong` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getLong(index: Int): ResultK<Long?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getLong(it) }

@Deprecated(message = "Use `getLong` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getLong(columnName: String): ResultK<Long?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getLong(it) }

private val EXPECTED_TYPES = ExpectedTypes("int8")
