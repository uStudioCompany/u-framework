package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement

public typealias SqlParameterSetter<T> = PreparedStatement.(index: Int, value: T?) -> Unit
