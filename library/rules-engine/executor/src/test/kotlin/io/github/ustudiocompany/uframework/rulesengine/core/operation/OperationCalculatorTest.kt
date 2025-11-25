package io.github.ustudiocompany.uframework.rulesengine.core.operation

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class OperationCalculatorTest : UnitTest() {

    init {

        "The extension function `calculate` for the `Operation` type" - {

            "when computing the operation is successful" - {
                val operation = TestOperation(
                    target = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                    value = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                    operator = EQ
                )

                "then the function should return the result" {
                    val result = operation.calculate(ENV_VARS, CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when computing the operation returns an error of computing the target" - {
                val operation = TestOperation(
                    target = Value.Expression(expression = EXPRESSION),
                    value = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                    operator = EQ
                )

                "then the function should return an error" {
                    val result = operation.calculate(ENV_VARS, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<CalculateOperationErrors.ComputingTarget>()
                }
            }

            "when computing the operation returns an error of computing the value" - {
                val operation = TestOperation(
                    target = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                    value = Value.Expression(expression = EXPRESSION),
                    operator = EQ
                )

                "then the function should return an error" {
                    val result = operation.calculate(ENV_VARS, CONTEXT)
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<CalculateOperationErrors.ComputingValue>()
                }
            }
        }
    }

    companion object {
        private val ENV_VARS = envVarsOf()
        private val CONTEXT = Context.empty()

        private const val VALUE_1 = "value-1"

        private val EXPRESSION = object : FeelExpression {

            override val text: String
                get() = "a/0"

            override fun evaluate(
                envVars: EnvVars,
                context: Context
            ): ResultK<JsonElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }

    private data class TestOperation(
        override val target: Value,
        override val operator: Operator<Boolean>,
        override val value: Value
    ) : Operation<Boolean>
}
