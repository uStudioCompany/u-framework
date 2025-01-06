package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes
import java.util.*

public fun Row.getUUID(index: Int): ResultK<UUID?, JDBCErrors> =
    this.extract(index, EXPECTED_TYPES) { getObject(it, UUID::class.java) }

public fun Row.getUUID(columnName: String): ResultK<UUID?, JDBCErrors> =
    this.extract(columnName, EXPECTED_TYPES) { getObject(it, UUID::class.java) }

private val EXPECTED_TYPES = ExpectedTypes("uuid")
