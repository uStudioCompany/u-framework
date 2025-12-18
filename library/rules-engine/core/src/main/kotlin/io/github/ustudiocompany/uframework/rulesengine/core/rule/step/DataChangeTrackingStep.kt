package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class DataChangeTrackingStep(
    public override val id: StepId,
    public override val condition: Condition,
    public val uri: Uiss,
    public val args: Args,
) : Step
