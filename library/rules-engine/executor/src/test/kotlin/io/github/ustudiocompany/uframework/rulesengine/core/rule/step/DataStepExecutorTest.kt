package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.Result.Action
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class DataStepExecutorTest : UnitTest() {

    init {

        "The data step executor" - {

            "when condition is missing" - {
                val condition: Condition? = null

                "then the executor should perform the step" - {
                    val context = Context.empty()
                    val step = createStep(condition)
                    val result = step.execute(context, TestMerger())

                    "then the executor should return a success result" {
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }

                    "then the context should contain the generated data" {
                        val result = context[SOURCE]
                        result.shouldBeSuccess()
                        result.value shouldBe EXPECTED_DATA
                    }
                }
            }

            "when condition is present" - {

                "when condition is satisfied" - {
                    val condition: Condition = satisfiedCondition()

                    "then the executor should perform the step" - {
                        val context = Context.empty()
                        val step = createStep(condition)
                        val result = step.execute(context, TestMerger())

                        "then the executor should return a success result" {
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }

                        "then the context should contain the generated data" {
                            val result = context[SOURCE]
                            result.shouldBeSuccess()
                            result.value shouldBe EXPECTED_DATA
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the executor should  not perform the step" - {
                        val context = Context.empty()
                        val step = createStep(condition)
                        val result = step.execute(context, TestMerger())

                        "then the executor should return a success result" {
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }

                        "then the context should not contain the generated data" {
                            context.contains(SOURCE) shouldBe false
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val TEXT_VALUE_1 = DataElement.Text("value-1")
        private val TEXT_VALUE_2 = DataElement.Text("value-2")
        private const val ID_DATA_KEY = "id"
        private const val ID_DATA_VALUE = "0000-0000-0000-0000"

        private val SOURCE = Source("output")

        private val EXPECTED_DATA = DataElement.Struct(
            mutableMapOf(
                ID_DATA_KEY to DataElement.Text(ID_DATA_VALUE)
            )
        )

        private fun satisfiedCondition() = Condition(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    value = Value.Literal(fact = TEXT_VALUE_1),
                    operator = EQ
                )
            )
        )

        private fun notSatisfiedCondition() = Condition(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    value = Value.Literal(fact = TEXT_VALUE_2),
                    operator = EQ
                )
            )
        )

        private fun createStep(condition: Condition?) =
            DataStep(
                condition = condition,
                dataScheme = DataScheme.Struct(
                    properties = listOf(
                        DataScheme.Property.Element(
                            name = ID_DATA_KEY,
                            value = Value.Literal(fact = DataElement.Text(ID_DATA_VALUE))
                        )
                    )
                ),
                result = Step.Result(
                    source = SOURCE,
                    action = Action.PUT
                )
            )
    }

    private class TestMerger : Merger {
        override fun merge(origin: DataElement, target: DataElement): ResultK<DataElement, Failure> {
            error("Not implemented")
        }
    }
}
