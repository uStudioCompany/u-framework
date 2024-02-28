package io.github.ustudiocompany.uframework.jdbc.sql.internal

import io.github.ustudiocompany.uframework.jdbc.sql.SqlType
import io.github.ustudiocompany.uframework.jdbc.sql.TypedParameter

internal class TypedParameterResolver private constructor(
    private val items: List<Pair<String, TypedParameter>>
) {

    fun resolve(name: String): TypedParameter =
        items.find { it.first.equals(name, ignoreCase = true) }
            ?.second
            ?: error("Unknown property name `$name`.")

    companion object {
        fun of(items: Map<String, SqlType>): TypedParameterResolver =
            TypedParameterResolver(
                items.map {
                    val name = it.key
                    ":$name" to TypedParameter(name = name, type = it.value)
                }
            )
    }
}
