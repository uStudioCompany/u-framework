package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ConditionSatisfiedTest : UnitTest() {

    init {

        "The extension function `isSatisfied` for the `Condition` type" - {

            "when the condition is null" - {
                val condition: Condition? = null

                "then the function should return the value true" {
                    val result = condition.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the condition is empty" - {
                val condition = Condition(emptyList())

                "then the function should return the value true" {
                    val result = condition.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the condition is not empty" - {

                "when all predicate are satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value true" {
                        val result = condition.isSatisfied(CONTEXT)
                        result shouldBeSuccess true
                    }
                }

                "when any predicate are not satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value false" {
                        val result = condition.isSatisfied(CONTEXT)
                        result shouldBeSuccess false
                    }
                }

                "when any predicate returns an error" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Expression(EXPRESSION),
                                value = Value.Literal(DataElement.Text(VALUE_1)),
                                operator = EQ
                            )
                        )
                    )

                    "then function should return an error" {
                        val result = condition.isSatisfied(CONTEXT)
                        result.shouldContainFailureInstance()
                            .shouldBeInstanceOf<CheckingConditionSatisfactionErrors>()
                    }
                }
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()

        private const val VALUE_1 = "value-1"
        private const val VALUE_2 = "value-2"

        private val EXPRESSION = object : FeelExpression {

            override val text: String
                get() = "a/0"

            override fun evaluate(
                context: Context
            ): ResultK<DataElement, FeelExpression.EvaluateError> =
                FeelExpression.EvaluateError(this).asFailure()
        }
    }
}
