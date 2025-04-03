package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Uri

public data class DataRetrieveStep(
    public override val condition: Condition?,
    public val uri: Uri,
    public val args: Args,
    public val result: Step.Result
) : Step
