package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import org.postgresql.util.PGobject
import java.sql.ResultSet

public fun ResultRow.getJSONBOrNull(column: Int): JDBCResult<String?> =
    this.extract(column, JSONB_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, PGobject::class.java)
        if (rs.wasNull()) ResultK.Success.asNull else result.value.asSuccess()
    }

public fun ResultRow.getJSONB(column: Int): JDBCResult<String> =
    ensureColumnValueNotNull(column, ResultRow::getJSONBOrNull)

private val JSONB_TYPE = Types("jsonb")
