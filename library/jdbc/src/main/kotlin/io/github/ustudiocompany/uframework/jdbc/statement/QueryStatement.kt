package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import java.sql.PreparedStatement
import java.sql.SQLException

public class QueryStatement internal constructor(private val statement: PreparedStatement) {

    public fun execute(): Result<Rows, JDBCErrors> = try {
        Rows(statement.executeQuery()).success()
    } catch (expected: SQLException) {
        val error = if (expected.isConnectionError)
            JDBCErrors.Connection(expected)
        else
            JDBCErrors.UnexpectedError(expected)
        error.error()
    } catch (expected: Exception) {
        JDBCErrors.UnexpectedError(expected).error()
    }
}
