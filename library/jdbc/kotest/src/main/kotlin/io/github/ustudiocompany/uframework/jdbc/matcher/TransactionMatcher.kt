package io.github.ustudiocompany.uframework.jdbc.matcher

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.matcher.shouldContainErrorInstance
import io.github.airflux.commons.types.fail.matcher.shouldContainExceptionInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

@OptIn(AirfluxTypesExperimental::class)
public inline fun <reified ErrorT : Any> TransactionResult<*, ErrorT>.shouldContainErrorInstance(): ErrorT =
    shouldContainFailureInstance()
        .shouldContainErrorInstance<ErrorT>()

@OptIn(AirfluxTypesExperimental::class)
public fun TransactionResult<*, *>.shouldContainExceptionInstance(): JDBCError =
    shouldContainFailureInstance()
        .shouldContainExceptionInstance()
