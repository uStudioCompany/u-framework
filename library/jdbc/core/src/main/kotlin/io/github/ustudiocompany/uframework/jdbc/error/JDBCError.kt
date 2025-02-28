package io.github.ustudiocompany.uframework.jdbc.error

import io.github.ustudiocompany.uframework.failure.Failure
import java.sql.SQLException

public class JDBCError(
    public override val description: String,
    exception: Throwable? = null
) : Failure {
    override val code: String = "JDBC-ERROR"

    override val cause: Failure.Cause = if (exception != null)
        Failure.Cause.Exception(exception)
    else
        Failure.Cause.None

    override val details: Failure.Details =
        if (exception is SQLException)
            Failure.Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
        else
            Failure.Details.NONE

    private companion object {
        private const val SQL_STATE_DETAILS_KEY = "sql-state"
    }
}
