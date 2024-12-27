package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.rule.ArgType.entries

public enum class ArgType(private val tag: String) {
    PATH_VARIABLE("pathVariable"),
    HEADER("header"),
    REQUEST_PARAMETER("requestParameter"),
    ;

    public companion object {

        @JvmStatic
        public fun of(value: String): ArgType = entries.first { it.tag.equals(value, true) }
    }
}
