package io.github.ustudiocompany.uframework.jdbc.error

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure
import java.sql.SQLException

public class JDBCError(
    public override val description: String,
    exception: Throwable? = null
) : Failure {
    override val code: String = "JDBC-ERROR"

    override val cause: Cause = if (exception != null)
        Cause.Exception(exception)
    else
        Cause.None

    override val details: Details =
        if (exception is SQLException)
            Details.of(SQL_STATE_DETAILS_KEY to exception.sqlState)
        else
            Details.NONE

    private companion object {
        private const val SQL_STATE_DETAILS_KEY = "sql-state"
    }
}
