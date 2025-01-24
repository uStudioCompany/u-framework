// This file was automatically generated from TransactionManager.kt by Knit tool. Do not edit.
package examples.exampleTransactionManager01

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.mapException
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.liftToTransactionError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRowMapper
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getInt
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.github.ustudiocompany.uframework.jdbc.row.mapToObject
import io.github.ustudiocompany.uframework.jdbc.row.resultRowMapper
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.IntSqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.query
import io.github.ustudiocompany.uframework.jdbc.statement.setParameters
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.jdbc.use

internal class UserRepository(private val tm: TransactionManager) {

    fun getUser(id: Int): ResultK<User?, Fail<User.Error, UserRepositoryError>> =
        tm.useTransaction { connection ->
            connection.namedPreparedStatement(SELECT_USER_SQL)
                .use { statement ->
                    statement.setParameters {
                        set("id", id, IntSqlParameterSetter)
                    }
                        .query()
                        .mapToObject(mapper)
                }
        }.mapFailure { fail ->
            fail.mapException { UserRepositoryError("Failed to get user", it) }
        }

    companion object {

        private val SELECT_USER_SQL =
            ParametrizedSql.of("SELECT id, name FROM users WHERE id = :id")

        private val mapper: ResultRowMapper<User, User.Error> =
            resultRowMapper { _, row ->
                val (id) = row.getInt(1)
                val (name) = row.getString(2)
                User.create(id, name).liftToTransactionError()
            }
    }
}

internal data class UserRepositoryError(val message: String, val cause: JDBCError)

internal class User private constructor(val id: Int, val name: String) {

    companion object {
        internal fun create(id: Int, name: String): ResultK<User, Error> =
            if (name.isNotBlank())
                User(id, name).asSuccess()
            else
                Error.asFailure()
    }

    internal object Error
}
