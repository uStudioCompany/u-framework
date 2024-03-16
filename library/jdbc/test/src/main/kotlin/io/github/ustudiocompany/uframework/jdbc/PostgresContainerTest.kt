package io.github.ustudiocompany.uframework.jdbc

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.failure
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

public abstract class PostgresContainerTest(dockerImageName: String = "postgres:15.4") : FreeSpec() {

    protected val container: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>(dockerImageName)
        .apply { startupAttempts = 1 }

    protected val dataSource: HikariDataSource = install(JdbcDatabaseContainerExtension(container))

    protected fun truncateTable(name: String) {
        executeSql("TRUNCATE TABLE $name CASCADE")
    }

    protected fun executeSql(value: String) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(value).execute()
            }
    }

    protected fun <T> checkData(sql: String, assertions: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            assertions(result, index++)
                        }
                    }
            }
    }

    protected fun selectData(sql: String): List<ResultSet> =
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        val list = mutableListOf<ResultSet>()
                        while (result.next()) {
                            list.add(result)
                        }
                        list
                    }
            }

    protected fun <T> shouldContain(sql: String, assertion: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            assertion(result, index++)
                        }
                    }
            }
    }

    protected fun shouldContainExactly(sql: String, vararg assertions: ResultSet.(index: Int) -> Unit) {
        val assertionCount = assertions.size
        var currentAssertionIndex = 0
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var index = 0
                        while (result.next()) {
                            if (currentAssertionIndex != assertionCount) {
                                val assertion = assertions[currentAssertionIndex]
                                assertion(result, index++)
                                currentAssertionIndex++
                            } else
                                failure("There are fewer assertions than rows.")
                        }
                    }
            }
    }
}
