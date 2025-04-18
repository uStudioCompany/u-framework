package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.liftToException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ValueR, ErrorT, ExceptionT> ResultK<ValueT, ExceptionT>.use(
    block: (ValueT) -> BiFailureResultK<ValueR, ErrorT, ExceptionT>
): BiFailureResultK<ValueR, ErrorT, ExceptionT>
    where ValueT : AutoCloseable,
          ErrorT : Any,
          ExceptionT : Any {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess())
        this.value.use { block(it) }
    else
        this.liftToException()
}
