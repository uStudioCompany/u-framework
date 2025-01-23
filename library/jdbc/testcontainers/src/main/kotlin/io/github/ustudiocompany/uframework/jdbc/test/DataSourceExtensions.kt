package io.github.ustudiocompany.uframework.jdbc.test

import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

public fun DataSource.truncateTable(name: String) {
    executeSql("TRUNCATE TABLE $name CASCADE")
}

public fun DataSource.executeSql(sql: String) {
    useStatement(isReadOnly = false) { statement ->
        statement.execute(sql)
    }
}

public fun <T> DataSource.checkData(sql: String, assertions: ResultSet.(index: Int) -> T) {
    useStatement(isReadOnly = true) { statement ->
        statement.executeQuery(sql)
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

public fun <T> DataSource.selectData(sql: String, mapper: ResultSet.(index: Int) -> T): List<T> =
    useStatement(isReadOnly = true) { statement ->
        statement.executeQuery(sql)
            .let { result ->
                val list = mutableListOf<T>()
                var index = 0
                while (result.next()) {
                    list.add(mapper(result, index++))
                }
                list
            }
    }

public fun DataSource.shouldBeEmpty(sql: String) {
    useStatement(isReadOnly = true) { statement ->
        statement.executeQuery(sql)
            .also { result ->
                result.next() shouldBe false
            }
    }
}

private inline fun <T> DataSource.useStatement(isReadOnly: Boolean, block: (statement: Statement) -> T): T =
    useConnection(isReadOnly) { connection ->
        connection.createStatement()
            .use { statement -> block(statement) }
    }

private inline fun <T> DataSource.useConnection(isReadOnly: Boolean, block: (connection: Connection) -> T): T =
    connection.use { connection ->
        block(connection.apply { this.isReadOnly = isReadOnly })
    }

private const val NO_ROWS_FOUND = "No rows found."
