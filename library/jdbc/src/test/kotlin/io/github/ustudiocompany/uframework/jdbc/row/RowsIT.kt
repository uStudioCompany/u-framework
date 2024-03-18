package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.airflux.functional.success
import io.github.airflux.functional.traverse
import io.github.ustudiocompany.uframework.jdbc.AbstractSQLDatabaseTest
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class RowsIT : AbstractSQLDatabaseTest() {

    init {

        "The Rows type" - {

            "when the table does not contain data" - {
                truncateTable(TABLE_NAME)

                "then the result should be empty" {
                    val result = executeQuery()

                    result.shouldBeSuccess()
                    result.value.isEmpty() shouldBe true
                }
            }

            "when the table contains data" - {
                truncateTable(TABLE_NAME)
                executeSql(
                    """
                       | INSERT INTO $TABLE_NAME($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
                       |      VALUES ('$FIRST_ROW_ID', '$FIRST_ROW_TITLE');
                       | INSERT INTO $TABLE_NAME($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
                       |      VALUES ('$SECOND_ROW_ID', '$SECOND_ROW_TITLE');
                    """.trimMargin()
                )

                "then the result should contain all data from the database" {
                    val result = executeQuery()

                    result.shouldBeSuccess()
                    result.value.isEmpty() shouldBe false
                    result.value shouldContainExactly listOf(
                        FIRST_ROW_ID to FIRST_ROW_TITLE,
                        SECOND_ROW_ID to SECOND_ROW_TITLE,
                    )
                }
            }
        }
    }

    private fun executeQuery() =
        dataSource.connection
            .use { connection ->
                val statement = connection.prepareStatement(SQL)
                val resultSet = statement.executeQuery()
                Rows(resultSet)
                    .traverse { row ->
                        val id = row.getString("id").getOrForward { return@traverse it }
                        val title = row.getString("title").getOrForward { return@traverse it }
                        Pair(id, title).success()
                    }
            }

    companion object {
        private const val TABLE_NAME = "test_row_set_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"
        private const val FIRST_ROW_ID = "f-r-id"
        private const val FIRST_ROW_TITLE = "f-r-title"
        private const val SECOND_ROW_ID = "s-r-id"
        private const val SECOND_ROW_TITLE = "s-r-title"

        private val SQL = """
            |   SELECT $ID_COLUMN_NAME, 
            |          $TITLE_COLUMN_NAME
            |     FROM $TABLE_NAME
            | ORDER BY id
        """.trimMargin()
    }
}
