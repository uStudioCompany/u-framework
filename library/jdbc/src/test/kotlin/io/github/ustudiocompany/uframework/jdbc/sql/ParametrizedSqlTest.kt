package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.datatest.withData
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ParametrizedSqlTest : UnitTest() {

    init {

        "The ParametrizedSql type" - {

            "when successful SQL parsing" - {
                withData(nameFn = { item -> item.description }, testData) { item ->
                    val parametrizedSql = ParametrizedSql.of(item.sqlWithParameters)
                    parametrizedSql.value shouldBe item.sqlWithoutParameters
                    parametrizedSql.parameters shouldContainExactly item.parameters
                }
            }

            "when failure SQL parsing" - {

                "when the SQL parameter is duplicate" - {

                    "then should throw exception" {
                        val sql = "SELECT * FROM table WHERE id = :$PARAM_1 AND id = :$PARAM_1"
                        val exception = shouldThrow<IllegalStateException> { ParametrizedSql.of(sql) }
                        exception.message shouldBe "The parameter `$PARAM_1` is duplicated."
                    }
                }

                "when the SQL parameter is empty" - {
                    withData(
                        listOf(
                            "SELECT * FROM table WHERE id = :",
                            "SELECT * FROM table WHERE id = : AND id = :$PARAM_1",
                            "SELECT * FROM table WHERE id = :$PARAM_1 AND id = :"
                        )
                    ) { sql ->
                        val exception = shouldThrow<IllegalStateException> { ParametrizedSql.of(sql) }
                        exception.message shouldBe "The parameter name is empty."
                    }
                }
            }
        }
    }

    private val testData = listOf(
        TestDataItem(
            description = "there are no parameters and SQL is empty",
            sqlWithParameters = "",
            sqlWithoutParameters = "",
            parameters = mapOf()
        ),
        TestDataItem(
            description = "there are no parameters and SQL without formatting",
            sqlWithParameters = "SELECT * FROM table",
            sqlWithoutParameters = "SELECT * FROM table",
            parameters = mapOf()
        ),
        TestDataItem(
            description = "there is one parameter and SQL without formatting",
            sqlWithParameters = "SELECT * FROM table WHERE id = :$PARAM_1",
            sqlWithoutParameters = "SELECT * FROM table WHERE id = ?",
            parameters = mapOf(PARAM_1 to 1)
        ),
        TestDataItem(
            description = "there are two parameters and SQL without formatting",
            sqlWithParameters = "SELECT * FROM table WHERE id = :$PARAM_1 AND name = :$PARAM_2",
            sqlWithoutParameters = "SELECT * FROM table WHERE id = ? AND name = ?",
            parameters = mapOf(PARAM_1 to 1, PARAM_2 to 2)
        ),
        TestDataItem(
            description = "there are no parameters and SQL with formatting",
            sqlWithParameters = """
                | SELECT * 
                |   FROM table
                """.trimMargin(),
            sqlWithoutParameters = """
                | SELECT * 
                |   FROM table
                """.trimMargin(),
            parameters = mapOf()
        ),
        TestDataItem(
            description = "there is one parameters and SQL with formatting",
            sqlWithParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = :$PARAM_1 
                """.trimMargin(),
            sqlWithoutParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = ? 
                """.trimMargin(),
            parameters = mapOf(PARAM_1 to 1)
        ),
        TestDataItem(
            description = "there are two parameters and SQL with formatting",
            sqlWithParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = :$PARAM_1
                |    AND name = :$PARAM_2
                """.trimMargin(),
            sqlWithoutParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = ?
                |    AND name = ?
                """.trimMargin(),
            parameters = mapOf(PARAM_1 to 1, PARAM_2 to 2)
        ),
    )

    private data class TestDataItem(
        val description: String,
        val sqlWithParameters: String,
        val sqlWithoutParameters: String,
        val parameters: Map<String, Int>,
    )

    private companion object {
        private const val PARAM_1 = "param-1"
        private const val PARAM_2 = "param_2"
    }
}
