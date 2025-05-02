package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult

public fun interface Merger {

    public fun merge(
        strategyCode: StepResult.Action.Merge.StrategyCode,
        dst: DataElement,
        src: DataElement
    ): ResultK<DataElement, Error>

    public class Error(
        message: String = "",
        override val cause: Failure.Cause = Failure.Cause.None,
        override val details: Failure.Details = Failure.Details.NONE
    ) : BasicRulesEngineError {
        override val code: String = PREFIX + "1"
        override val description: String =
            "The error of merging data." + if (message.isNotEmpty()) " $message" else ""

        private companion object {
            private const val PREFIX = "DATA-MERGE-"
        }
    }
}
