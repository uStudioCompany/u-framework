package io.github.ustudiocompany.uframework.jdbc.error

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public typealias ErrorConverter<F> = (JDBCErrors) -> F
