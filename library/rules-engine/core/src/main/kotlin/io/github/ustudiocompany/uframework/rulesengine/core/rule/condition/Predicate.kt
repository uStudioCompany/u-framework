package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator

public data class Predicate(
    override val target: Value,
    override val operator: Operator<Boolean>,
    override val value: Value
) : Operation<Boolean>
