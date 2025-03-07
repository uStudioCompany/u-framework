package io.github.ustudiocompany.uframework.jdbc.row

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor

public interface ResultRow {

    /**
     * Example: the boolean type extractor.
     * <!--- INCLUDE
     * import io.github.airflux.commons.types.resultk.ResultK
     * import io.github.airflux.commons.types.resultk.asSuccess
     * import io.github.ustudiocompany.uframework.jdbc.JDBCResult
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
     * import java.sql.ResultSet
     * -->
     * ```kotlin
     * public fun ResultRow.getBoolean(column: Int): JDBCResult<Boolean?> =
     *     this.extract(column, ColumnTypes("bool")) { column: Int, rs: ResultSet ->
     *         val result = rs.getBoolean(column)
     *         if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
     *     }
     * ```
     * <!--- KNIT example-extract-value-of-the-boolean-type-01.kt -->
     */
    public fun <ValueT> extract(
        index: Int,
        expectedColumnTypes: ColumnTypes,
        block: DataExtractor<ValueT>
    ): JDBCResult<ValueT>

    @JvmInline
    public value class ColumnTypes(private val names: List<String>) {

        public constructor(vararg name: String) : this(name.toList())

        public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

        override fun toString(): String = names.toString()
    }
}
