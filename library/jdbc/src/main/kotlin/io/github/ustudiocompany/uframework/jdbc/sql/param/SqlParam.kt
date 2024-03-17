package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public fun PreparedStatement.setValue(position: Int, value: SqlParam) {
    with(value) {
        setValue(position)
    }
}

public abstract class SqlParam {
    public abstract val name: String
    public abstract fun PreparedStatement.setValue(position: Int)
}
