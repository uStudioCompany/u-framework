package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.RuleId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.ConditionEvaluationErrors

public sealed class RuleExecutionErrors(ruleId: RuleId) : BasicRulesEngineError {

    override val details: Failure.Details = Failure.Details.of(RULE_ID to ruleId.get)

    public class ConditionEvaluation internal constructor(
        ruleId: RuleId,
        cause: ConditionEvaluationErrors
    ) : RuleExecutionErrors(ruleId) {
        override val code: String = PREFIX + "1"
        override val description: String = "Error evaluating condition the rule '${ruleId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class Execution internal constructor(
        ruleId: RuleId,
        cause: StepExecutionErrors
    ) : RuleExecutionErrors(ruleId) {
        override val code: String = PREFIX + "2"
        override val description: String = "Error execution the rule '${ruleId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULE-EXECUTION-"
        private const val RULE_ID = "rule-id"
    }
}
