package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes

public fun Row.getBoolean(index: Int): ResultK<Boolean?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getBoolean(it) }

public fun Row.getBoolean(columnName: String): ResultK<Boolean?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getBoolean(it) }

private val EXPECTED_TYPES = ExpectedTypes("bool")
