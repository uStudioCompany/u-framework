package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Conditional

public sealed interface Step : Conditional {

    public data class ErrorCode(val get: String)

    public data class Result(val source: Source, val action: Action) {

        public enum class Action {
            PUT,
            REPLACE,
            MERGE
        }
    }
}
