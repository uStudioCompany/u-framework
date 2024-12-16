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

        override val description: String = "Unexpected error: '${exception.message}'."

        override val cause: Failure.Cause = Failure.Cause.Exception(exception)

        override val details: Failure.Details =
            if (exception is SQLException)
                Failure.Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
            else
                Failure.Details.NONE
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
                Failure.Details.NONE
    }

    public sealed class Row : JDBCErrors() {
        override val domain: String
            get() = super.domain + "-ROW"

        public class UndefinedColumn(public val label: ColumnLabel) : Row() {
            public constructor(name: String) : this(label = ColumnLabel.Name(name))
            public constructor(index: Int) : this(ColumnLabel.Index(index))

            override val number: String = "1"

            override val description: String = "Undefined column."

            override val details: Failure.Details = mutableListOf<Failure.Details.Item>()
                .apply {
                    add(Failure.Details.Item(key = label.detailsKey, value = label.detailsValue))
                }
                .let { Failure.Details.of(it) }
        }

        public class TypeMismatch(
            public val label: ColumnLabel,
            public val expected: String,
            public val actual: String
        ) : Row() {

            public constructor(name: String, expected: String, actual: String) :
                this(ColumnLabel.Name(name), expected, actual)

            public constructor(index: Int, expected: String, actual: String) :
                this(ColumnLabel.Index(index), expected, actual)

            override val number: String = "2"

            override val description: String = "The type of a column is mismatched."

            override val details: Failure.Details = mutableListOf<Failure.Details.Item>()
                .apply {
                    add(Failure.Details.Item(key = label.detailsKey, value = label.detailsValue))
                    add(Failure.Details.Item(key = EXPECTED_COLUMN_TYPE_DETAILS_KEY, value = expected))
                    add(Failure.Details.Item(key = ACTUAL_COLUMN_TYPE_DETAILS_KEY, value = actual))
                }
                .let { Failure.Details.of(it) }
        }

        public class ReadColumn(public val label: ColumnLabel, cause: Exception) : Row() {

            public constructor(name: String, cause: Exception) : this(ColumnLabel.Name(name), cause)
            public constructor(index: Int, cause: Exception) : this(ColumnLabel.Index(index), cause)

            override val number: String = "3"

            override val description: String = "The error of reading column value"

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
                    Failure.Details.NONE
        }
    }

    private companion object {
        private const val SQL_STATE_DETAILS_KEY = "sql-state"
        private const val EXPECTED_COLUMN_TYPE_DETAILS_KEY = "expected-column-type"
        private const val ACTUAL_COLUMN_TYPE_DETAILS_KEY = "actual-column-type"
    }
}
