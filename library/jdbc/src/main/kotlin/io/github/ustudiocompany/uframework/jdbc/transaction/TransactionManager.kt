package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.use

/**
 * A transaction manager that provides a way to start a transaction.
 *
 * Example:
 * <!--- INCLUDE
 * import io.github.airflux.commons.types.fail.Fail
 * import io.github.airflux.commons.types.fail.mapException
 * import io.github.airflux.commons.types.resultk.ResultK
 * import io.github.airflux.commons.types.resultk.asFailure
 * import io.github.airflux.commons.types.resultk.asSuccess
 * import io.github.airflux.commons.types.resultk.mapFailure
 * import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
 * import io.github.ustudiocompany.uframework.jdbc.liftToTransactionError
 * import io.github.ustudiocompany.uframework.jdbc.row.ResultRowMapper
 * import io.github.ustudiocompany.uframework.jdbc.row.extractor.getInt
 * import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
 * import io.github.ustudiocompany.uframework.jdbc.row.mapToObject
 * import io.github.ustudiocompany.uframework.jdbc.row.resultRowMapper
 * import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
 * import io.github.ustudiocompany.uframework.jdbc.sql.parameter.intSqlParameterSetter
 * import io.github.ustudiocompany.uframework.jdbc.statement.query
 * import io.github.ustudiocompany.uframework.jdbc.statement.setParameters
 * import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
 * import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
 * import io.github.ustudiocompany.uframework.jdbc.use
 * -->
 * ```kotlin
 * internal class UserRepository(private val tm: TransactionManager) {
 *
 *     fun getUser(id: Int): ResultK<User?, Fail<User.Error, UserRepositoryError>> =
 *         tm.useTransaction { connection ->
 *             connection.namedPreparedStatement(SELECT_USER_SQL)
 *                 .use { statement ->
 *                     statement.setParameters {
 *                         set("id", id, intSqlParameterSetter)
 *                     }
 *                         .query()
 *                         .mapToObject(mapper)
 *                 }
 *         }.mapFailure { fail ->
 *             fail.mapException { UserRepositoryError("Failed to get user", it) }
 *         }
 *
 *     companion object {
 *
 *         private val SELECT_USER_SQL =
 *             ParametrizedSql.of("SELECT id, name FROM users WHERE id = :id")
 *
 *         private val mapper: ResultRowMapper<User, User.Error> =
 *             resultRowMapper { _, row ->
 *                 val (id) = row.getInt(1)
 *                 val (name) = row.getString(2)
 *                 User.create(id, name).liftToTransactionError()
 *             }
 *     }
 * }
 *
 * internal data class UserRepositoryError(val message: String, val cause: JDBCError)
 *
 * internal class User private constructor(val id: Int, val name: String) {
 *
 *     companion object {
 *         internal fun create(id: Int, name: String): ResultK<User, Error> =
 *             if (name.isNotBlank())
 *                 User(id, name).asSuccess()
 *             else
 *                 Error.asFailure()
 *     }
 *
 *     internal object Error
 * }
 * ```
 * <!--- KNIT example-transaction-manager-01.kt -->
 */
public interface TransactionManager {

    /**
     * Starts a new transaction with the given isolation level and read-only mode.
     *
     * @param isolation The transaction isolation level.
     * @param readOnly The read-only mode.
     * @return An object that allows you to manage a running transaction, or an error if the transaction cannot be
     * started.
     */
    public fun startTransaction(
        isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
        readOnly: Boolean = false
    ): JDBCResult<Transaction>
}

public inline fun <ValueT, ErrorT> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JdbcConnection) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> =
    startTransaction(isolation)
        .use { tx ->
            val result = try {
                block(tx.connection)
            } catch (expected: Exception) {
                transactionIncident(
                    description = "Error while executing transaction block",
                    exception = expected
                )
            }

            if (result.isSuccess())
                tx.commit()
                    .fold(
                        onSuccess = { result },
                        onFailure = { error -> transactionIncident(error) }
                    )
            else {
                tx.rollback()
                result
            }
        }
