package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.RuleId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors

public sealed interface RuleExecuteErrors : BasicRulesEngineError {

    public class CheckingConditionSatisfactionRule internal constructor(
        ruleId: RuleId,
        cause: CheckingConditionSatisfactionErrors
    ) : RuleExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error checking condition satisfaction of the rule '$ruleId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            RULE_ID to ruleId.get
        )
    }

    public class ExecutionRule internal constructor(
        ruleId: RuleId,
        cause: StepExecuteError
    ) : RuleExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "The error of execution the rule '$ruleId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            RULE_ID to ruleId.get
        )
    }

    private companion object {
        private const val PREFIX = "RULE-EXECUTION-"
        private const val RULE_ID = "rule-id"
    }
}
