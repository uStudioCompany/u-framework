package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class DataBuildStep(
    public override val id: StepId,
    override val condition: Condition,
    public val dataSchema: DataSchema,
    public val result: StepResult
) : Step
