package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.fail.asException
import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JBDCConnection
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.use

/**
 * A transaction manager that provides a way to start a transaction.
 *
 * Example:
 * <!--- INCLUDE
 * import io.github.airflux.commons.types.resultk.BiFailureResultK
 * import io.github.airflux.commons.types.resultk.ResultK
 * import io.github.airflux.commons.types.resultk.asFailure
 * import io.github.airflux.commons.types.resultk.asSuccess
 * import io.github.airflux.commons.types.resultk.liftToError
 * import io.github.airflux.commons.types.resultk.mapException
 * import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
 * import io.github.ustudiocompany.uframework.jdbc.row.ResultRowMapper
 * import io.github.ustudiocompany.uframework.jdbc.row.extractor.getInt
 * import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
 * import io.github.ustudiocompany.uframework.jdbc.row.mapToObject
 * import io.github.ustudiocompany.uframework.jdbc.row.resultRowMapper
 * import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
 * import io.github.ustudiocompany.uframework.jdbc.sql.parameter.IntSqlParameterSetter
 * import io.github.ustudiocompany.uframework.jdbc.statement.query
 * import io.github.ustudiocompany.uframework.jdbc.statement.setParameters
 * import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
 * import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
 * import io.github.ustudiocompany.uframework.jdbc.use
 * -->
 * ```kotlin
 * internal class UserRepository(private val tm: TransactionManager) {
 *
 *     fun getUser(id: Int): BiFailureResultK<User?, User.Error, UserRepositoryError> =
 *         tm.useTransaction { connection ->
 *             connection.namedPreparedStatement(SELECT_USER_SQL)
 *                 .use { statement ->
 *                     statement.setParameters {
 *                         set("id", id, IntSqlParameterSetter)
 *                     }
 *                         .query()
 *                         .mapToObject(mapper)
 *                 }
 *         }.mapException { fail ->
 *             UserRepositoryError("Failed to get user", fail)
 *         }
 *
 *     companion object {
 *
 *         private val SELECT_USER_SQL =
 *             ParametrizedSql.of("SELECT id, name FROM users WHERE id = :id")
 *
 *         private val mapper: ResultRowMapper<User, User.Error, JDBCError> =
 *             resultRowMapper { _, row ->
 *                 val (id) = row.getInt(1)
 *                 val (name) = row.getString(2)
 *                 User.create(id, name).liftToError()
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

public inline fun <ValueT, ErrorT : Any> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JBDCConnection) -> TransactionResult<ValueT, ErrorT, JDBCError>
): TransactionResult<ValueT, ErrorT, JDBCError> =
    useTransaction(isolation, { it }, block)

public inline fun <ValueT, ErrorT : Any, ExceptionT : Any> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    exceptionBuilder: (JDBCError) -> ExceptionT,
    block: (JBDCConnection) -> BiFailureResultK<ValueT, ErrorT, ExceptionT>
): BiFailureResultK<ValueT, ErrorT, ExceptionT> =
    startTransaction(isolation)
        .mapFailure { fail -> exceptionBuilder(fail) }
        .use { tx ->
            val result = try {
                block(tx.connection)
            } catch (expected: Exception) {
                val error = JDBCError(description = "Error while executing transaction block", exception = expected)
                exceptionBuilder(error).asException().asFailure()
            }

            if (result.isSuccess())
                tx.commit()
                    .fold(
                        onSuccess = { result },
                        onFailure = { error -> exceptionBuilder(error).asException().asFailure() }
                    )
            else {
                tx.rollback()
                result
            }
        }
