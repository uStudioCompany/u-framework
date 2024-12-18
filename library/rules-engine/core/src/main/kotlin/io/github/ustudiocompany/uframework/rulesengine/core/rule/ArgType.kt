package io.github.ustudiocompany.uframework.rulesengine.core.rule

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
