package io.github.ustudiocompany.uframework.jdbc.exception

import org.postgresql.util.PSQLState
import java.sql.SQLException

@PublishedApi
internal val SQLException.isConnectionError: Boolean
    get() = PSQLState.isConnectionError(sqlState)

@PublishedApi
internal val SQLException.isDuplicate: Boolean
    get() = sqlState == PSQLState.UNIQUE_VIOLATION.state

@PublishedApi
internal val SQLException.isUndefinedColumn: Boolean
    get() = sqlState == PSQLState.UNDEFINED_COLUMN.state

@PublishedApi
internal val SQLException.isCustom: Boolean
    get() = sqlState.startsWith("U")
