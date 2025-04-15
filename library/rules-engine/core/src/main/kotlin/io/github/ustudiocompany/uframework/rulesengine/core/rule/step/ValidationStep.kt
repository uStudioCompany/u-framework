package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator

public data class ValidationStep(
    public override val condition: Condition,
    override val target: Value,
    override val operator: Operator<Boolean>,
    override val value: Value?,
    public val errorCode: ErrorCode
) : Step, Operation<Boolean> {

    public data class ErrorCode(val get: String)
}
