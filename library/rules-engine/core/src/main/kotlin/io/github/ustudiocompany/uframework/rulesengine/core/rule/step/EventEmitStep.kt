package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class EventEmitStep(
    override val condition: Condition,
    public val args: Args
) : Step
