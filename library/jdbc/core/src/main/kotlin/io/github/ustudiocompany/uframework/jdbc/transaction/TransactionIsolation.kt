package io.github.ustudiocompany.uframework.jdbc.transaction

import java.sql.Connection

public enum class TransactionIsolation(public val get: Int) {
    DEFAULT(-1),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
}
