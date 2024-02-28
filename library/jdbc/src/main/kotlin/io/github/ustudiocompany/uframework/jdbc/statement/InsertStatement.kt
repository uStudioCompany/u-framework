package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isDuplicate
import java.sql.PreparedStatement
import java.sql.SQLException

public class InsertStatement internal constructor(private val statement: PreparedStatement) {

    public fun execute(): Result<Boolean, JDBCErrors> = try {
        Result.of(statement.executeUpdate() > 0)
    } catch (expected: SQLException) {
        val error = when {
            expected.isConnectionError -> JDBCErrors.Connection(expected)
            expected.isDuplicate -> JDBCErrors.Data.DuplicateKeyValue(expected)
            else -> JDBCErrors.UnexpectedError(expected)
        }
        error.error()
    } catch (expected: Exception) {
        JDBCErrors.UnexpectedError(expected).error()
    }
}
