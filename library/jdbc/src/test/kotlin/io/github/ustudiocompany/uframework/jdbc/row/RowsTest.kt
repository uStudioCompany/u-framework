package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.intellij.lang.annotations.Language

internal class RowsTest : IntegrationTest() {

    private val container = PostgresContainerTest()

    init {

        "The Rows type" - {
            container.executeSql(CREATE_TABLE)

            "when the table does not contain data" - {
                container.truncateTable(TABLE_NAME)

                "then the result should be empty" {
                    val result = executeQuery()

                    result.shouldBeSuccess()
                    result.value.shouldBeEmpty()
                }
            }

            "when the table contains data" - {
                container.truncateTable(TABLE_NAME)
                container.executeSql(
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
                    result.value shouldContainExactly listOf(
                        FIRST_ROW_ID to FIRST_ROW_TITLE,
                        SECOND_ROW_ID to SECOND_ROW_TITLE,
                    )
                }
            }
        }
    }

    private fun executeQuery() =
        container.dataSource
            .connection
            .use { connection ->
                val statement = connection.prepareStatement(SQL)
                val resultSet = statement.executeQuery()
                Rows(resultSet)
                    .traverse { row ->
                        val id = row.getString("id").getOrForward { return@traverse it }
                        val title = row.getString("title").getOrForward { return@traverse it }
                        Pair(id, title).asSuccess()
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

        @JvmStatic
        @Language("Postgresql")
        private val CREATE_TABLE = """
            | CREATE TABLE $TABLE_NAME (
            |    $ID_COLUMN_NAME    TEXT NOT NULL,
            |    $TITLE_COLUMN_NAME TEXT NOT NULL,
            |    PRIMARY KEY ($ID_COLUMN_NAME)
            | );
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val SQL = """
            |   SELECT $ID_COLUMN_NAME, 
            |          $TITLE_COLUMN_NAME
            |     FROM $TABLE_NAME
            | ORDER BY id
        """.trimMargin()
    }
}
