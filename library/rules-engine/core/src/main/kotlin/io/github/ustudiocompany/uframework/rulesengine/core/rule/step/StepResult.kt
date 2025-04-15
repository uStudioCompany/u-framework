package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public data class StepResult(
    public val source: Source,
    public val action: Action
) {

    public enum class Action(public val key: String) {
        PUT("put"),
        REPLACE("replace"),
        MERGE("merge");

        public companion object {
            public fun orNull(value: String): Action? =
                entries.firstOrNull { it.key.equals(value, ignoreCase = true) }
        }
    }
}
