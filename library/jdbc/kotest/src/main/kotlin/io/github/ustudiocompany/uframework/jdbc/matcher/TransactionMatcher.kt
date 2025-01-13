package io.github.ustudiocompany.uframework.jdbc.matcher

import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.either.matcher.shouldBeLeft
import io.github.airflux.commons.types.either.matcher.shouldBeRight
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.kotest.matchers.types.shouldBeInstanceOf

public inline fun <reified E : Any> TransactionResult<*, E>.shouldBeError(): E {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Either<E, JDBCError>>()
    failure.shouldBeLeft()
    return failure.value.shouldBeInstanceOf<E>()
}

public fun TransactionResult<*, *>.shouldBeIncident(): JDBCError {
    shouldBeFailure()
    val failure = cause.shouldBeInstanceOf<Either<*, JDBCError>>()
    failure.shouldBeRight()
    return failure.value.shouldBeInstanceOf<JDBCError>()
}
