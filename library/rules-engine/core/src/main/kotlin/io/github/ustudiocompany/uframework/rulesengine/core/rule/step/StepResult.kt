package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source

public data class StepResult(
    public val source: Source,
    public val action: Action
) {

    public sealed interface Action {
        public data object Put : Action
        public data object Replace : Action
        public data class Merge(public val strategyCode: StrategyCode) : Action {
            public class StrategyCode(public val get: String)
        }
    }
}
