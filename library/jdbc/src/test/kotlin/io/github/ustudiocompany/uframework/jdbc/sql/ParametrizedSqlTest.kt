package io.github.ustudiocompany.uframework.jdbc.sql

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe

internal class ParametrizedSqlTest : FreeSpec() {

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
                        val sql = "SELECT * FROM table WHERE id = :id AND id = :id"
                        val exception = shouldThrow<IllegalStateException> { ParametrizedSql.of(sql) }
                        exception.message shouldBe "The parameter `id` is duplicated."
                    }
                }

                "when the SQL parameter is empty" - {
                    withData(
                        listOf(
                            "SELECT * FROM table WHERE id = :",
                            "SELECT * FROM table WHERE id = : AND id = :id",
                            "SELECT * FROM table WHERE id = :id AND id = :"
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
            sqlWithParameters = "SELECT * FROM table WHERE id = :id",
            sqlWithoutParameters = "SELECT * FROM table WHERE id = ?",
            parameters = mapOf("id" to 1)
        ),
        TestDataItem(
            description = "there are two parameters and SQL without formatting",
            sqlWithParameters = "SELECT * FROM table WHERE id = :id AND name = :name",
            sqlWithoutParameters = "SELECT * FROM table WHERE id = ? AND name = ?",
            parameters = mapOf("id" to 1, "name" to 2)
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
                |  WHERE id = :id 
                """.trimMargin(),
            sqlWithoutParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = ? 
                """.trimMargin(),
            parameters = mapOf("id" to 1)
        ),
        TestDataItem(
            description = "there are two parameters and SQL with formatting",
            sqlWithParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = :id 
                |    AND name = :name
                """.trimMargin(),
            sqlWithoutParameters = """
                | SELECT * 
                |   FROM table
                |  WHERE id = ? 
                |    AND name = ?
                """.trimMargin(),
            parameters = mapOf("id" to 1, "name" to 2)
        ),
    )

    private data class TestDataItem(
        val description: String,
        val sqlWithParameters: String,
        val sqlWithoutParameters: String,
        val parameters: Map<String, Int>,
    )
}
