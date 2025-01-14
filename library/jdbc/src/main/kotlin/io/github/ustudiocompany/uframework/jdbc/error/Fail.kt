package io.github.ustudiocompany.uframework.jdbc.error

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.exception

public typealias Error<ErrorT> = Fail<ErrorT, Nothing>
public typealias Incident = Fail<Nothing, JDBCError>

public fun incident(value: JDBCError): Incident = exception(value)
