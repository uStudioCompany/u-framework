package io.github.ustudiocompany.uframework.jdbc.row

import io.github.ustudiocompany.uframework.jdbc.AbstractSQLDatabaseTest
import org.intellij.lang.annotations.Language
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

internal abstract class AbstractRowTest : AbstractSQLDatabaseTest() {

    protected fun <T> executeQuery(block: Row.() -> T): T =
        dataSource.connection
            .use { connection ->
                val statement = connection.prepareStatement(SELECT_QUERY)
                val resultSet = statement.executeQuery()
                val rows = Rows(resultSet)
                val row = rows.first()
                block(row)
            }

    companion object {

        @JvmStatic
        protected val TABLE_NAME = "test_row_table"

        @JvmStatic
        protected val UNKNOWN_COLUMN_NAME = "unknown_column"

        @JvmStatic
        protected val BOOLEAN_COLUMN_NAME = "boolean_column"

        @JvmStatic
        protected val BOOLEAN_COLUMN_VALUE = true

        @JvmStatic
        protected val STRING_COLUMN_NAME = "string_column"

        @JvmStatic
        protected val STRING_COLUMN_VALUE = "string_value"

        @JvmStatic
        protected val INT_COLUMN_NAME = "int_column"

        @JvmStatic
        protected val INT_COLUMN_VALUE = Int.MAX_VALUE

        @JvmStatic
        protected val LONG_COLUMN_NAME = "long_column"

        @JvmStatic
        protected val LONG_COLUMN_VALUE = Long.MAX_VALUE

        @JvmStatic
        protected val UUID_COLUMN_NAME = "uuid_column"

        @JvmStatic
        protected val UUID_COLUMN_VALUE: UUID = UUID.randomUUID()

        @JvmStatic
        protected val TIMESTAMP_COLUMN_NAME = "timestamp_column"

        @JvmStatic
        protected val TIMESTAMP_COLUMN_VALUE: Timestamp = Timestamp.valueOf(LocalDateTime.now())

        @JvmStatic
        protected val UNKNOWN_COLUMN_INDEX = 0

        @JvmStatic
        protected val BOOLEAN_COLUMN_INDEX = 1

        @JvmStatic
        protected val STRING_COLUMN_INDEX = 2

        @JvmStatic
        protected val INT_COLUMN_INDEX = 3

        @JvmStatic
        protected val LONG_COLUMN_INDEX = 4

        @JvmStatic
        protected val UUID_COLUMN_INDEX = 5

        @JvmStatic
        protected val TIMESTAMP_COLUMN_INDEX = 6

        @JvmStatic
        @Language("Postgresql")
        protected val INSERT_QUERY = """
            | INSERT INTO $TABLE_NAME (
            |    $BOOLEAN_COLUMN_NAME,
            |    $STRING_COLUMN_NAME,
            |    $INT_COLUMN_NAME,
            |    $LONG_COLUMN_NAME,
            |    $UUID_COLUMN_NAME,
            |    $TIMESTAMP_COLUMN_NAME)
            | VALUES (
            |     $BOOLEAN_COLUMN_VALUE,
            |     '$STRING_COLUMN_VALUE'::text,
            |     $INT_COLUMN_VALUE::int,
            |     $LONG_COLUMN_VALUE,
            |     '$UUID_COLUMN_VALUE'::uuid,
            |     '$TIMESTAMP_COLUMN_VALUE'::timestamp
            | );
            """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        protected val SELECT_QUERY = """
            | SELECT $BOOLEAN_COLUMN_NAME, 
            |        $STRING_COLUMN_NAME,
            |        $INT_COLUMN_NAME,
            |        $LONG_COLUMN_NAME,
            |        $UUID_COLUMN_NAME,
            |        $TIMESTAMP_COLUMN_NAME
            |   FROM $TABLE_NAME
        """.trimMargin()
    }
}
