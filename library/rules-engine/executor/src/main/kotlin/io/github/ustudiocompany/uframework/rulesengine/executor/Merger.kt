package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult

public fun interface Merger {

    public fun merge(
        strategyCode: StepResult.Action.Merge.StrategyCode,
        dst: JsonElement,
        src: JsonElement
    ): ResultK<JsonElement, Error>

    public class Error private constructor(
        message: String = "",
        override val cause: Failure.Cause,
        override val details: Failure.Details = Failure.Details.NONE
    ) : BasicRulesEngineError {

        public constructor() : this(
            message = "",
            cause = Failure.Cause.None,
            details = Failure.Details.NONE
        )

        public constructor(cause: Failure) :
            this(
                cause = Failure.Cause.Failure(cause)
            )

        public constructor(
            message: String,
            exception: Throwable? = null,
            details: Failure.Details = Failure.Details.NONE
        ) :
            this(
                message = message,
                cause = if (exception != null)
                    Failure.Cause.Exception(exception)
                else
                    Failure.Cause.None,
                details = details
            )

        override val code: String = PREFIX + "1"
        override val description: String =
            "The error of merging data." + if (message.isNotEmpty()) " $message" else ""

        private companion object {
            private const val PREFIX = "DATA-MERGE-"
        }
    }
}
