package io.github.ustudiocompany.uframework.jdbc.error

public data class JDBCError(
    public val description: String,
    public val exception: Throwable?
) {
    public constructor(description: String) : this(description, null)
}
