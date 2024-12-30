package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.ErrorCode

public data class ValidationStep(
    public override val condition: Condition?,
    public val target: Value,
    public val compareWith: Value,
    public val comparator: Comparator,
    public val errorCode: ErrorCode
) : Step
