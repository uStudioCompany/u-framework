package io.github.ustudiocompany.uframework.jdbc.error

import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.either.left
import io.github.airflux.commons.types.either.right

public typealias Fail<ErrorT, IncidentT> = Either<ErrorT, IncidentT>
public typealias Error<ErrorT> = Either<ErrorT, Nothing>
public typealias Incident = Either<Nothing, JDBCError>

public fun <ErrorT> error(value: ErrorT): Error<ErrorT> = left(value)
public fun incident(value: JDBCError): Incident = right(value)
