package io.github.ustudiocompany.uframework.jdbc.statement

public interface JdbcStatement : AutoCloseable {

    public sealed class Timeout {
        public data object Default : Timeout()
        public data class Seconds(val value: Int) : Timeout()
    }
}
