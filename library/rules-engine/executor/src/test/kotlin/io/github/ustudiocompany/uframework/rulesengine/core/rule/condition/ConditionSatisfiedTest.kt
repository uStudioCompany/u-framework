package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ConditionSatisfiedTest : UnitTest() {

    init {

        "The extension function `isSatisfied` for the `Condition` type" - {

            "when the condition is missing" - {
                val condition = Condition.NONE

                "then the function should return the value true" {
                    val result = condition.isSatisfied(ENV_VARS, CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the condition is not empty" - {

                "when all predicate are satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                                value = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = JsonElement.Text(VALUE_2)),
                                value = Value.Literal(fact = JsonElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value true" {
                        val result = condition.isSatisfied(ENV_VARS, CONTEXT)
                        result shouldBeSuccess true
                    }
                }

                "when any predicate are not satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = JsonElement.Text(VALUE_1)),
                                value = Value.Literal(fact = JsonElement.Text(VALUE_2)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = JsonElement.Text(VALUE_2)),
                                value = Value.Literal(fact = JsonElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value false" {
                        val result = condition.isSatisfied(ENV_VARS, CONTEXT)
                        result shouldBeSuccess false
                    }
                }

                "when any predicate returns an error" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Expression(EXPRESSION),
                                value = Value.Literal(JsonElement.Text(VALUE_1)),
                                operator = EQ
                            )
                        )
                    )

                    "then function should return an error" {
                        val result = condition.isSatisfied(ENV_VARS, CONTEXT)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<CheckingConditionSatisfactionErrors>()
                    }
                }
            }
        }
    }

    companion object {
        private val ENV_VARS = EnvVars.EMPTY
        private val CONTEXT = Context.empty()

        private const val VALUE_1 = "value-1"
        private const val VALUE_2 = "value-2"

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
}
