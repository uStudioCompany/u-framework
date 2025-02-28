package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.ResultSet

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public interface Row {

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun <T> extract(
        index: Int,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(index: Int) -> T
    ): ResultK<T?, JDBCErrors>

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun <T> extract(
        columnName: String,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(index: Int) -> T
    ): ResultK<T?, JDBCErrors>

    @JvmInline
    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public value class ExpectedTypes(private val names: List<String>) {

        public constructor(vararg name: String) : this(name.toList())

        public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

        override fun toString(): String = names.joinToString(prefix = "'", postfix = "'", separator = ", ")
    }
}
