package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes

@Deprecated(message = "Use `getString` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getString(index: Int): ResultK<String?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getString(it) }

@Deprecated(message = "Use `getString` for ResultRow type instead", level = DeprecationLevel.WARNING)
public fun Row.getString(columnName: String): ResultK<String?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getString(it) }

private val EXPECTED_TYPES = ExpectedTypes("text", "varchar", "bpchar")
