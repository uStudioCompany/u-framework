package io.github.ustudiocompany.uframework.jdbc.matcher

import io.github.airflux.commons.types.fail.matcher.shouldBeError
import io.github.airflux.commons.types.fail.matcher.shouldBeException
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.ustudiocompany.uframework.jdbc.error.Error
import io.github.ustudiocompany.uframework.jdbc.error.Incident
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.kotest.matchers.types.shouldBeInstanceOf

public inline fun <reified E : Any> TransactionResult<*, E>.shouldBeError(): E {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Error<E>>()
    failure.shouldBeError()
    return failure.value.shouldBeInstanceOf<E>()
}

public fun TransactionResult<*, *>.shouldBeIncident(): JDBCError {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Incident>()
    failure.shouldBeException()
    return failure.value.shouldBeInstanceOf<JDBCError>()
}
