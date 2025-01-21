package io.github.ustudiocompany.uframework.jdbc.row

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor

public interface ResultRow {

    /**
     * Example: the boolean type extractor.
     * <!--- INCLUDE
     * import io.github.ustudiocompany.uframework.jdbc.JDBCResult
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
     * import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
     * import java.sql.ResultSet
     * -->
     * ```kotlin
     * public fun ResultRow.getBoolean(column: Int): JDBCResult<Boolean?> =
     *     this.extract(column, Types("bool")) { column: Int, rs: ResultSet ->
     *         val result = rs.getBoolean(column)
     *         if (rs.wasNull()) null else result
     *     }
     * ```
     * <!--- KNIT example-extract-value-of-the-boolean-type-01.kt -->
     */
    public fun <ValueT> extract(index: Int, types: Types, block: DataExtractor<ValueT>): JDBCResult<ValueT?>

    @JvmInline
    public value class Types(private val names: List<String>) {

        public constructor(vararg name: String) : this(name.toList())

        public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

        override fun toString(): String = names.toString()
    }
}
