package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.row.ResultRowsInstance
import java.sql.PreparedStatement

internal fun PreparedStatement.tryExecute(): JDBCResult<StatementResult> = ResultK.catch(
    catch = { exception -> JDBCError(description = "Error while executing the statement.", exception = exception) },
    block = {
        val hasRows = execute()
        if (hasRows)
            StatementResult.Rows(ResultRowsInstance(resultSet))
        else
            StatementResult.Count(updateCount)
    }
)

internal fun PreparedStatement.tryExecuteQuery(): JDBCResult<ResultRows> = ResultK.catch(
    catch = { exception -> JDBCError(description = "Error while executing the query.", exception = exception) },
    block = {
        val result = executeQuery()
        ResultRowsInstance(result)
    }
)

internal fun PreparedStatement.tryExecuteUpdate(): JDBCResult<Int> = ResultK.catch(
    catch = { exception -> JDBCError(description = "Error while executing the update.", exception = exception) },
    block = { executeUpdate() }
)
