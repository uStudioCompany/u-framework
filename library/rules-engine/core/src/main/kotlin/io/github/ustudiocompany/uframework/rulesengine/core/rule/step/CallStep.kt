package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Method

public data class CallStep(
    public override val condition: Condition?,
    public val method: Method,
    public val args: Args,
    public val result: Step.Result
) : Step
