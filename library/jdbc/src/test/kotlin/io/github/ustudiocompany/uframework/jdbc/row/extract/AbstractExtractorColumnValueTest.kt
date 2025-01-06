package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.row.RowsInstance
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest

internal abstract class AbstractExtractorColumnValueTest : IntegrationTest() {

    protected val container = PostgresContainerTest()

    protected fun <T> executeQuery(sql: String, block: Row.() -> T): T =
        container.dataSource
            .connection
            .use { connection ->
                val statement = connection.prepareStatement(sql)
                val resultSet = statement.executeQuery()
                val rows: Rows = RowsInstance(resultSet)
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
