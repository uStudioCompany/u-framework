package io.github.ustudiocompany.uframework.jdbc.matcher

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.kotest.matchers.types.shouldBeInstanceOf

public inline fun <reified ErrorT : Any> TransactionResult<*, ErrorT>.shouldBeError(): ErrorT {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Fail.Error<ErrorT>>()
    failure.shouldBeError()
    return failure.value.shouldBeInstanceOf<ErrorT>()
}

public fun TransactionResult<*, *>.shouldBeException(): JDBCError {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Fail.Exception<JDBCError>>()
    failure.shouldBeException()
    return failure.value.shouldBeInstanceOf<JDBCError>()
}
