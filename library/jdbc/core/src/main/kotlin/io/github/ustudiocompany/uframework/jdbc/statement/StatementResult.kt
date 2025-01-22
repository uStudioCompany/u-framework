package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.row.ResultRows

public sealed interface StatementResult {
    public data class Count(val get: Int) : StatementResult
    public data class Rows(val get: ResultRows) : StatementResult
}
