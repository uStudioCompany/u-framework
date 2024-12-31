package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operator

public data class Predicate(
    override val target: Value,
    override val operator: Operator,
    override val value: Value
) : Operation
