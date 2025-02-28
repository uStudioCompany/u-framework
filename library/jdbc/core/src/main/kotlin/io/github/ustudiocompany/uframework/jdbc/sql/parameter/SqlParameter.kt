package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement

public fun interface SqlParameter {
    public fun setValue(statement: PreparedStatement, index: Int)
}
