package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.Raise
import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.fail.Fail.Companion.exception
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.withRaise
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public typealias ResultRowMapper<ValueT, ErrorT> =
    (index: Int, row: ResultRow) -> TransactionResult<ValueT, ErrorT, JDBCError>

public fun <ValueT, ErrorT : Any> resultRowMapper(
    block: ResultRowMapperScope.(Int, ResultRow) -> TransactionResult<ValueT, ErrorT, JDBCError>
): ResultRowMapper<ValueT, ErrorT> =
    { index, row ->
        val scope = ResultRowMapperScopeInstance()
        withRaise(scope, wrap = { error -> exception(error).asFailure() }) {
            block(this, index, row)
        }
    }

public interface ResultRowMapperScope {
    public operator fun <ValueT> JDBCResult<ValueT>.component1(): ValueT = bind()
    public fun <ValueT> JDBCResult<ValueT>.bind(): ValueT
}

@PublishedApi
internal class ResultRowMapperScopeInstance : ResultRowMapperScope, Raise<JDBCError> {

    override fun <ValueT> JDBCResult<ValueT>.bind(): ValueT =
        if (this.isSuccess()) value else doRaise(cause)

    override fun raise(error: JDBCError): Nothing {
        doRaise(error)
    }
}
