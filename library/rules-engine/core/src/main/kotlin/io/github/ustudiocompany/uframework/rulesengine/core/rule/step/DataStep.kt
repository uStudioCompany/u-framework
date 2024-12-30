package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class DataStep(
    override val condition: Condition?,
    public val dataScheme: DataScheme,
    public val result: Step.Result
) : Step
