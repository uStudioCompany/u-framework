package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates

public data class DataStep(
    public override val predicate: Predicates?,
    public val dataScheme: DataScheme,
    public val result: Step.Result
) : Step
