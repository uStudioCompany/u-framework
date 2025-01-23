package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.mapOrIncident
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.mapToList
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.jdbc.use
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.core.extensions.install
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

internal class MapToListTest : IntegrationTest() {

    init {

        "The extension function `mapToList`" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE)

            "when the execution is successful" - {
                dataSource.truncateTable(TABLE_NAME)
                dataSource.executeSql(INSERT_SQL)
                val result = tm.execute(SELECT_SQL) { statement ->
                    statement.query()
                        .mapToList { index, row ->
                            row.getString(TITLE_COLUMN_INDEX)
                                .mapOrIncident { value -> (index to value).asSuccess() }
                        }
                }

                "then the result should contain only the selected data" {
                    result.shouldBeSuccess()
                    result.value.size shouldBe 2
                    result.value shouldContainExactly listOf(
                        FIRST_ROW_INDEX to TITLE_FIRST_ROW_VALUE,
                        SECOND_ROW_INDEX to TITLE_SECOND_ROW_VALUE
                    )
                }
            }

            "when the execution is failed" - {

                "when the parameter is not specified" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.query()
                            .mapToList<String, Nothing> { index, row ->
                                error("Error")
                            }
                    }

                    "then the result should contain an incident" {
                        result.shouldBeIncident()
                    }
                }
            }
        }
    }

    private fun ResultRow.getString(column: Int) =
        extract(column, TEXT_TYPE) { col, rs -> rs.getString(col).asSuccess() }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val ID_SECOND_ROW_VALUE = "s-r-id"

        private const val TITLE_FIRST_ROW_VALUE = "f-r-title"
        private const val TITLE_SECOND_ROW_VALUE = "s-r-title"

        private const val TITLE_COLUMN_INDEX = 2
        private const val FIRST_ROW_INDEX = 1
        private const val SECOND_ROW_INDEX = 2

        private val TEXT_TYPE = ResultRow.Types("text", "varchar", "bpchar")

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
        private val INSERT_SQL = """
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
            | VALUES ('$ID_FIRST_ROW_VALUE', '$TITLE_FIRST_ROW_VALUE');
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
            | VALUES ('$ID_SECOND_ROW_VALUE', '$TITLE_SECOND_ROW_VALUE');
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val SELECT_SQL = """
            |   SELECT $ID_COLUMN_NAME,
            |          $TITLE_COLUMN_NAME
            |     FROM $TABLE_NAME
        """.trimMargin()

        private fun <ValueT, ErrorT> TransactionManager.execute(
            sql: String,
            block: (statement: JBDCPreparedStatement) -> TransactionResult<ValueT, ErrorT>
        ): TransactionResult<ValueT, ErrorT> =
            useTransaction { connection ->
                connection.preparedStatement(sql)
                    .use { statement -> block(statement) }
            }
    }
}
