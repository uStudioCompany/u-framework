package io.github.ustudiocompany.uframework.jdbc

import com.dream.umbrella.lib.kotest.IntegrationTest
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.failure
import io.kotest.core.extensions.install
import io.kotest.extensions.testcontainers.JdbcDatabaseContainerExtension
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

public abstract class PostgresContainerTest(dockerImageName: String = "postgres:15.4") : IntegrationTest() {

    protected val container: Lazy<PostgreSQLContainer<Nothing>> = lazy {
        PostgreSQLContainer<Nothing>(dockerImageName)
            .apply { startupAttempts = 1 }
    }

    protected val dataSource: Lazy<HikariDataSource> = lazy {
        install(JdbcDatabaseContainerExtension(container.value))
    }

    protected fun truncateTable(name: String) {
        executeSql("TRUNCATE TABLE $name CASCADE")
    }

    protected fun executeSql(value: String) {
        dataSource.value
            .connection
            .use { connection ->
                connection.prepareStatement(value).execute()
            }
    }

    protected fun <T> checkData(sql: String, assertions: ResultSet.(index: Int) -> T) {
        dataSource.value
            .connection
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
        dataSource.value
            .connection
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
        dataSource.value
            .connection
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
        dataSource.value
            .connection
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
