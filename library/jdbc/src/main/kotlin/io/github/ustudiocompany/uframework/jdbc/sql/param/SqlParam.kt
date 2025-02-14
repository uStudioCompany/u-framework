package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun PreparedStatement.setValue(position: Int, value: SqlParam) {
    with(value) {
        setValue(position)
    }
}

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public abstract class SqlParam {
    public abstract val name: String
    public abstract fun PreparedStatement.setValue(position: Int)
}
