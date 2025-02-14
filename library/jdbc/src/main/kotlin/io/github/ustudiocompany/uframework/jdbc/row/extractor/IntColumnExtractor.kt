package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes

@Deprecated(message = "Use `getInt` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getInt(index: Int): ResultK<Int?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getInt(it) }

@Deprecated(message = "Use `getInt` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getInt(columnName: String): ResultK<Int?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getInt(it) }

private val EXPECTED_TYPES = ExpectedTypes("int4")
