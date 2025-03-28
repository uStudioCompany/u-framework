package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Conditional

public sealed interface Step : Conditional {

    public data class Result(
        public val source: Source,
        public val action: Action
    ) {

        public enum class Action(private val key: String) {
            PUT("put"),
            REPLACE("replace"),
            MERGE("merge");

            public companion object {
                public fun orNull(value: String): Action? =
                    Action.entries.firstOrNull { it.key.equals(value, ignoreCase = true) }
            }
        }
    }
}
