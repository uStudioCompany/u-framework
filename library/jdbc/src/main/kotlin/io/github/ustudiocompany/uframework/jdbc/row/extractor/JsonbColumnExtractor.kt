package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes
import org.postgresql.util.PGobject

public fun Row.getJsonb(index: Int): ResultK<String?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getObject(it, PGobject::class.java).value }

public fun Row.getJsonb(columnName: String): ResultK<String?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getObject(it, PGobject::class.java).value }

private val EXPECTED_TYPES = ExpectedTypes("jsonb")
