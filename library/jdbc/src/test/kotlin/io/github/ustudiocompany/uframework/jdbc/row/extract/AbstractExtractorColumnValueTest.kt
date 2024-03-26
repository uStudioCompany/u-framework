package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Rows

internal abstract class AbstractExtractorColumnValueTest : PostgresContainerTest() {

    protected fun <T> executeQuery(sql: String, block: Row.() -> T): T =
        dataSource.value
            .connection
            .use { connection ->
                val statement = connection.prepareStatement(sql)
                val resultSet = statement.executeQuery()
                val rows = Rows(resultSet)
                val row = rows.first()
                block(row)
            }

    companion object {

        @JvmStatic
        val INVALID_COLUMN_INDEX = 0

        @JvmStatic
        val INVALID_COLUMN_NAME = "invalid_column"
    }
}
