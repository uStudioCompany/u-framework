package io.github.ustudiocompany.uframework.rulesengine.executor.incident

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.RuleId

public sealed class RuleExecutionIncident(ruleId: RuleId) : BasicRulesEngineIncident {

    override val details: Failure.Details = Failure.Details.of(RULE_ID to ruleId.get)

    public class Execution internal constructor(
        ruleId: RuleId,
        cause: StepExecutionIncident
    ) : RuleExecutionIncident(ruleId) {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident execution the rule '${ruleId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULE-EXECUTION-INCIDENT-"
        private const val RULE_ID = "rule-id"
    }
}
