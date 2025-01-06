package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes
import java.sql.Timestamp

public fun Row.getTimestamp(index: Int): ResultK<Timestamp?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getTimestamp(it) }

public fun Row.getTimestamp(columnName: String): ResultK<Timestamp?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getTimestamp(it) }

private val EXPECTED_TYPES = ExpectedTypes("timestamp")
