package io.github.ustudiocompany.uframework.jdbc.exception

import org.postgresql.util.PSQLState
import java.sql.SQLException

internal val SQLException.isConnectionError: Boolean
    get() = PSQLState.isConnectionError(sqlState)

internal val SQLException.isDuplicate: Boolean
    get() = sqlState == PSQLState.UNIQUE_VIOLATION.state

internal val SQLException.isInvalidColumnIndex: Boolean
    get() = sqlState == PSQLState.INVALID_PARAMETER_VALUE.state

internal val SQLException.isUndefinedColumn: Boolean
    get() = sqlState == PSQLState.UNDEFINED_COLUMN.state
