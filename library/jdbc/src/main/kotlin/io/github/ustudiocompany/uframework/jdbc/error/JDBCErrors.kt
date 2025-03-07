package io.github.ustudiocompany.uframework.jdbc.error

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.fullCode
import io.github.ustudiocompany.uframework.failure.fullDescription
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import java.sql.SQLException

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public sealed class JDBCErrors : Failure {

    override fun toString(): String =
        "JDBCErrors(" +
            "code=`${fullCode()}`, " +
            "description='${fullDescription()}', " +
            "cause=`$cause`, " +
            "details=$details" +
            ")"

    public class Unexpected(public val exception: Throwable) : JDBCErrors() {
        override val code: String = PREFIX + "1"

        override val description: String = "Unexpected error: '${exception.message}'."

        override val cause: Cause = Cause.Exception(exception)

        override val details: Details =
            if (exception is SQLException)
                Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Details.NONE
    }

    public class Connection(exception: Exception) : JDBCErrors() {
        override val code: String = PREFIX + "CONNECTION-1"
        override val description: String = "The connection error."
        override val cause: Cause = Cause.Exception(exception)
        override val details: Details =
            if (exception is SQLException)
                Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Details.NONE
    }

    public sealed class Row : JDBCErrors() {

        public class UndefinedColumn(public val label: ColumnLabel) : Row() {
            override val code: String = PREFIX + "ROW-1"
            override val description: String = "Undefined column."
            override val details: Details = mutableListOf<Details.Item>()
                .apply {
                    add(Details.Item(key = label.detailsKey, value = label.detailsValue))
                }
                .let { Details.of(it) }

            public constructor(name: String) : this(label = ColumnLabel.Name(name))
            public constructor(index: Int) : this(ColumnLabel.Index(index))
        }

        public class TypeMismatch(
            public val label: ColumnLabel,
            public val expected: String,
            public val actual: String
        ) : Row() {
            override val code: String = PREFIX + "ROW-2"
            override val description: String = "The type of a column is mismatched."
            override val details: Details = mutableListOf<Details.Item>()
                .apply {
                    add(Details.Item(key = label.detailsKey, value = label.detailsValue))
                    add(Details.Item(key = EXPECTED_COLUMN_TYPE_DETAILS_KEY, value = expected))
                    add(Details.Item(key = ACTUAL_COLUMN_TYPE_DETAILS_KEY, value = actual))
                }
                .let { Details.of(it) }

            public constructor(name: String, expected: String, actual: String) :
                this(ColumnLabel.Name(name), expected, actual)

            public constructor(index: Int, expected: String, actual: String) :
                this(ColumnLabel.Index(index), expected, actual)
        }

        public class ReadColumn(public val label: ColumnLabel, cause: Throwable) : Row() {
            override val code: String = PREFIX + "ROW-3"
            override val description: String = "The error of reading column value"
            override val cause: Cause = Cause.Exception(cause)
            override val details: Details = mutableListOf<Details.Item>()
                .apply {
                    add(Details.Item(key = label.detailsKey, value = label.detailsValue))
                    if (cause is SQLException)
                        add(Details.Item(key = SQL_STATE_DETAILS_KEY, value = cause.sqlState))
                }
                .let { Details.of(it) }

            public constructor(name: String, cause: Throwable) : this(ColumnLabel.Name(name), cause)
            public constructor(index: Int, cause: Throwable) : this(ColumnLabel.Index(index), cause)
        }
    }

    public sealed class Data : JDBCErrors() {

        public class DuplicateKeyValue(exception: Throwable) : Data() {
            override val code: String = PREFIX + "DATA-1"
            override val description: String = "The duplicate key value violates a unique constraint."
            override val cause: Cause = Cause.Exception(exception)
            override val details: Details =
                if (exception is SQLException)
                    Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
                else
                    Details.NONE
        }
    }

    @Deprecated("Use UnexpectedError instead.")
    public class Custom(public val state: String, exception: Throwable) : JDBCErrors() {
        override val code: String = PREFIX + "CUSTOM"
        override val description: String = ""
        override val cause: Cause = Cause.Exception(exception)
        override val details: Details =
            if (exception is SQLException)
                Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Details.NONE
    }

    private companion object {
        private const val PREFIX = "JDBC-"
        private const val SQL_STATE_DETAILS_KEY = "sql-state"
        private const val EXPECTED_COLUMN_TYPE_DETAILS_KEY = "expected-column-type"
        private const val ACTUAL_COLUMN_TYPE_DETAILS_KEY = "actual-column-type"
    }
}
