package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.data.DataScheme

public data class DataBuildStep(
    override val condition: Condition?,
    public val dataScheme: DataScheme,
    public val result: Step.Result
) : Step
