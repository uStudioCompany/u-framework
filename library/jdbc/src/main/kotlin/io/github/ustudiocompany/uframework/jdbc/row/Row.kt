package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.ResultSet

public interface Row {

    public fun <T> extract(
        index: Int,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(index: Int) -> T
    ): ResultK<T?, JDBCErrors>

    public fun <T> extract(
        columnName: String,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(index: Int) -> T
    ): ResultK<T?, JDBCErrors>

    @JvmInline
    public value class ExpectedTypes(private val names: List<String>) {

        public constructor(vararg name: String) : this(name.toList())

        public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

        override fun toString(): String = names.joinToString(prefix = "'", postfix = "'", separator = ", ")
    }
}
