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
    ): ResultK<DataElement, Errors.Merge>

    public sealed interface Errors : BasicRulesEngineError {

        public class Merge(
            message: String = "",
            exception: Throwable? = null,
            override val details: Failure.Details = Failure.Details.NONE
        ) : Errors {
            override val code: String = PREFIX + "1"
            override val description: String =
                "The error of merging data." + if (message.isNotEmpty()) " $message" else ""
            override val cause: Failure.Cause =
                if (exception == null)
                    Failure.Cause.None
                else
                    Failure.Cause.Exception(exception)
        }

        private companion object {
            private const val PREFIX = "DATA-MERGE-"
        }
    }
}
