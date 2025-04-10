package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition

public data class DataRetrieveStep(
    public override val condition: Condition?,
    public val uri: Uri,
    public val args: Args,
    public val result: StepResult
) : Step
