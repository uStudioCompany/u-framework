package io.github.ustudiocompany.uframework.rulesengine.core.operation

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator
import io.github.ustudiocompany.uframework.rulesengine.executor.error.FeelExpressionError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class OperationCalculatorTest : UnitTest() {

    init {

        "The extension function `calculate` for the `Operation` type" - {

            "when computing the operation is successful" - {
                val operation = TestOperation(
                    target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    value = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    operator = EQ
                )

                "then the function should return the result" {
                    val result = operation.calculate(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when computing the operation returns an error" - {
                val operation = TestOperation(
                    target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                    value = Value.Expression(expression = EXPRESSION),
                    operator = EQ
                )

                "then the function should return an error" {
                    val result = operation.calculate(CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpressionError>()
                }
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()

        private const val VALUE_1 = "value-1"

        private val EXPRESSION = FeelExpression {
            FeelExpression.Errors.Evaluate("a/0").asFailure()
        }
    }

    private data class TestOperation(
        override val target: Value,
        override val operator: Operator<Boolean>,
        override val value: Value
    ) : Operation<Boolean>
}
