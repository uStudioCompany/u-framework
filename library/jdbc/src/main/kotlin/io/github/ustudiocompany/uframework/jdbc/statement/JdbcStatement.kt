package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionIncident
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public interface JdbcStatement : AutoCloseable {

    public sealed class Timeout {
        public data object Default : Timeout()
        public data class Seconds(val value: Int) : Timeout()
    }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ErrorT, StatementT : JdbcStatement> JDBCResult<StatementT>.useStatement(
    block: (StatementT) -> TransactionResult<ValueT, ErrorT>
): ResultK<ValueT, Fail<ErrorT, JDBCError>> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value.use { block(it) } else transactionIncident(this.cause)
}
