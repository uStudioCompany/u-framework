package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class DataBuildStep(
    override val condition: Condition?,
    public val dataScheme: DataScheme,
    public val result: StepResult
) : Step
