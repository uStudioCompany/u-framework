package io.github.ustudiocompany.uframework.jdbc.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import java.sql.SQLException

public sealed class JDBCErrors : Failure {

    override val domain: String
        get() = "JDBC"

    override fun toString(): String =
        "JDBCErrors(" +
            "code=`${code()}`, " +
            "description='${joinDescriptions()}', " +
            "cause=`$cause`, " +
            "details=$details" +
            ")"

    public class UnexpectedError(exception: Exception) : JDBCErrors() {
        override val number: String = "1"

        override val description: String = "Unexpected error: `${exception.message}`."

        override val cause: Failure.Cause = Failure.Cause.Exception(exception)

        override val details: Failure.Details =
            if (exception is SQLException)
                Failure.Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Failure.Details.None
    }

    public class Connection(exception: Exception) : JDBCErrors() {
        override val domain: String
            get() = super.domain + "-CONNECTION"

        override val number: String = "1"
        override val description: String = "The connection error."
        override val cause: Failure.Cause = Failure.Cause.Exception(exception)
        override val details: Failure.Details =
            if (exception is SQLException)
                Failure.Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Failure.Details.None
    }

    public sealed class Rows : JDBCErrors() {
        override val domain: String
            get() = super.domain + "-ROWS"

        public class UndefinedColumn(public val label: ColumnLabel, cause: Exception) : Rows() {
            public constructor(name: String, cause: Exception) : this(label = ColumnLabel.Name(name), cause = cause)
            public constructor(index: Int, cause: Exception) : this(ColumnLabel.Index(index), cause)

            override val number: String = "1"

            override val description: String
                get() = "Undefined column."

            override val cause: Failure.Cause = Failure.Cause.Exception(cause)

            override val details: Failure.Details = mutableListOf<Failure.Details.Item>()
                .apply {
                    add(Failure.Details.Item(key = label.detailsKey, value = label.detailsValue))
                    if (cause is SQLException)
                        add(Failure.Details.Item(key = SQL_STATE_DETAILS_KEY, value = cause.sqlState))
                }
                .let { Failure.Details.of(it) }
        }
    }

    public sealed class Row : JDBCErrors() {
        override val domain: String
            get() = super.domain + "-ROW"

        public class ReadColumn(public val label: ColumnLabel, cause: Exception) : Row() {

            public constructor(name: String, cause: Exception) : this(ColumnLabel.Name(name), cause)
            public constructor(index: Int, cause: Exception) : this(ColumnLabel.Index(index), cause)

            override val number: String = "1"

            override val description: String
                get() = "The error of reading column value"

            override val cause: Failure.Cause = Failure.Cause.Exception(cause)

            override val details: Failure.Details = mutableListOf<Failure.Details.Item>()
                .apply {
                    add(Failure.Details.Item(key = label.detailsKey, value = label.detailsValue))
                    if (cause is SQLException)
                        add(Failure.Details.Item(key = SQL_STATE_DETAILS_KEY, value = cause.sqlState))
                }
                .let { Failure.Details.of(it) }
        }
    }

    public sealed class Data : JDBCErrors() {
        override val domain: String
            get() = super.domain + "-DATA"

        public class DuplicateKeyValue(exception: Exception) : Data() {
            override val number: String = "1"
            override val description: String = "The duplicate key value violates a unique constraint."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: Failure.Details =
                if (exception is SQLException)
                    Failure.Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
                else
                    Failure.Details.None
        }
    }

    private companion object {
        private const val SQL_STATE_DETAILS_KEY = "sql-state"
    }
}
