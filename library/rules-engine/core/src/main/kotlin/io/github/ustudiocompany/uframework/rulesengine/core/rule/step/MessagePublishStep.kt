package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class MessagePublishStep(
    public override val id: StepId,
    override val condition: Condition,
    public val routeKey: Value?,
    public val headers: MessageHeaders,
    public val body: Value?
) : Step
