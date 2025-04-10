package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.operator

internal typealias OperatorModel = String

internal enum class Operators(val key: String) {
    CONTAINS(key = "contains"),
    EQ(key = "eq"),
    IN(key = "in"),
    IS_PRESENT(key = "isPresent"),
    ;

    companion object {

        @JvmStatic
        fun orNull(value: String): Operators? = entries.firstOrNull { it.key.equals(value, true) }
    }
}
