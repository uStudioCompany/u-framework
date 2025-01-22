package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement

public typealias SqlParameterSetter<ValueT> = PreparedStatement.(index: Int, value: ValueT?) -> Unit
