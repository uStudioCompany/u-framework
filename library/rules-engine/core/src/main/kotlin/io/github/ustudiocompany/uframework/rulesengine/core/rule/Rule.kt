package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Conditional
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps

public data class Rule(
    override val predicate: Predicates?,
    public val steps: Steps
) : Conditional
