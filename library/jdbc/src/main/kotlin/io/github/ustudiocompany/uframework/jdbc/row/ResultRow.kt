package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractorWith

public interface ResultRow {

    /**
     * Example: the boolean type extractor.
     * <!--- INCLUDE
     * import io.github.airflux.commons.types.resultk.Success
     * import io.github.airflux.commons.types.resultk.asSuccess
     * import io.github.ustudiocompany.uframework.jdbc.JDBCResult
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
     * import java.sql.ResultSet
     * -->
     * ```kotlin
     * public fun <ErrorT> ResultRow.getBoolean(column: Int): JDBCResult<Boolean?> =
     *     this.extractWith(column, Types("bool")) { column: Int, rs: ResultSet ->
     *         val result = rs.getBoolean(column)
     *         if (rs.wasNull()) Success.asNull else result.asSuccess()
     *     }
     * ```
     */
    public fun <ValueT> extractWith(index: Int, types: Types, block: DataExtractorWith<ValueT>): JDBCResult<ValueT?>

    @JvmInline
    public value class Types(private val names: List<String>) {

        public constructor(vararg name: String) : this(name.toList())

        public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

        override fun toString(): String = names.toString()
    }
}

/**
 * Example: the boolean type extractor.
 * <!--- INCLUDE
 * import io.github.airflux.commons.types.resultk.Success
 * import io.github.airflux.commons.types.resultk.asSuccess
 * import io.github.ustudiocompany.uframework.jdbc.JDBCResult
 * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
 * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
 * import java.sql.ResultSet
 * -->
 * ```kotlin
 * public fun ResultRow.getBoolean(column: Int): JdbcResult<Boolean?> =
 *     this.extract(column, Types("bool")) { column: Int, rs: ResultSet ->
 *         val result = rs.getBoolean(column)
 *         if (rs.wasNull()) null else result
 *     }
 * ```
 */

public fun <ValueT> ResultRow.extract(
    index: Int,
    types: ResultRow.Types,
    block: DataExtractor<ValueT>
): JDBCResult<ValueT?> =
    extractWith(index, types) { col, rs -> block(col, rs).asSuccess() }
