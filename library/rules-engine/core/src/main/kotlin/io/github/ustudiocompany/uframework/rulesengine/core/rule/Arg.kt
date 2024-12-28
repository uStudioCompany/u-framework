package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Arg.Type.entries

public data class Arg(
    public val name: String,
    public val type: Type,
    public val value: Value
) {

    public enum class Type(private val tag: String) {
        PATH_VARIABLE("pathVariable"),
        HEADER("header"),
        REQUEST_PARAMETER("requestParameter"),
        ;

        public companion object {

            @JvmStatic
            public fun of(value: String): Type = entries.first { it.tag.equals(value, true) }
        }
    }
}
