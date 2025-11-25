package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Conditional
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps

public data class Rule(
    public val id: RuleId,
    override val condition: Condition,
    public val steps: Steps
) : Conditional
