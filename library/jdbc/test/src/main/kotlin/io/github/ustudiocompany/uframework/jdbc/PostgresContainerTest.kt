package io.github.ustudiocompany.uframework.jdbc

import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet
import javax.sql.DataSource

public class PostgresContainerTest(dockerImageName: String = "postgres:15.4") {

    private val container: Lazy<PostgreSQLContainer<Nothing>> = lazy {
        PostgreSQLContainer<Nothing>(dockerImageName)
            .apply {
                startupAttempts = 1
                start()
            }
    }

    private val _dataSource: Lazy<HikariDataSource> = lazy {
        HikariDataSource().apply {
            jdbcUrl = container.value.jdbcUrl
            username = container.value.username
            password = container.value.password
        }
    }

    public val dataSource: DataSource
        get() = _dataSource.value

    public fun truncateTable(name: String) {
        executeSql("TRUNCATE TABLE $name CASCADE")
    }

    public fun executeSql(value: String) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(value).execute()
            }
    }

    public fun <T> checkData(sql: String, assertions: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var hasResult = result.next()
                        if (!hasResult) throw failure(NO_ROWS_FOUND)
                        var index = 0
                        while (hasResult) {
                            assertions(result, index++)
                            hasResult = result.next()
                        }
                    }
            }
    }

    public fun shouldBeEmpty(sql: String) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .also { result ->
                        result.next() shouldBe false
                    }
            }
    }

    public fun <T> selectData(sql: String, mapper: ResultSet.(index: Int) -> T): List<T> =
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        val list = mutableListOf<T>()
                        var index = 0
                        while (result.next()) {
                            list.add(mapper(result, index++))
                        }
                        list
                    }
            }

    public fun <T> shouldContain(sql: String, assertion: ResultSet.(index: Int) -> T) {
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var hasResult = result.next()
                        if (!hasResult) throw failure(NO_ROWS_FOUND)
                        var index = 0
                        while (hasResult) {
                            assertion(result, index++)
                            hasResult = result.next()
                        }
                    }
            }
    }

    public fun shouldContainExactly(sql: String, vararg assertions: ResultSet.(index: Int) -> Unit) {
        val assertionCount = assertions.size
        var currentAssertionIndex = 0
        dataSource.connection
            .use { connection ->
                connection.prepareStatement(sql)
                    .executeQuery()
                    .let { result ->
                        var hasResult = result.next()
                        if (!hasResult) throw failure(NO_ROWS_FOUND)
                        var index = 0
                        while (hasResult) {
                            if (currentAssertionIndex != assertionCount) {
                                val assertion = assertions[currentAssertionIndex]
                                assertion(result, index++)
                                currentAssertionIndex++
                            } else
                                throw failure("There are fewer assertions than rows.")

                            hasResult = result.next()
                        }
                    }
            }
    }

    private companion object {
        private const val NO_ROWS_FOUND = "No rows found."
    }
}
