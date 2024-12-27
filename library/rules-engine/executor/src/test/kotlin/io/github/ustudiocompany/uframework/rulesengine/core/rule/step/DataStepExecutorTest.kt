package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.DataScheme
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.Result.Action
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class DataStepExecutorTest : UnitTest() {

    init {

        "The data step executor" - {

            "when predicate is missing" - {
                val predicate: Predicates? = null

                "then the executor should perform the step" - {
                    val context = Context.empty()
                    val step = requirementIsTrue(predicate)
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

            "when predicate is present" - {

                "when predicate is satisfied" - {
                    val predicate: Predicates = satisfiedPredicate()

                    "then the executor should perform the step" - {
                        val context = Context.empty()
                        val step = requirementIsTrue(predicate)
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

                "when predicate is not satisfied" - {
                    val predicate: Predicates = notSatisfiedPredicate()

                    "then the executor should  not perform the step" - {
                        val context = Context.empty()
                        val step = requirementIsTrue(predicate)
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

    companion object {
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

        private fun satisfiedPredicate() = Predicates(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    compareWith = Value.Literal(fact = TEXT_VALUE_1),
                    comparator = EQ
                )
            )
        )

        private fun notSatisfiedPredicate() = Predicates(
            listOf(
                Predicate(
                    target = Value.Literal(fact = TEXT_VALUE_1),
                    compareWith = Value.Literal(fact = TEXT_VALUE_2),
                    comparator = EQ
                )
            )
        )

        private fun requirementIsTrue(predicate: Predicates?) =
            Step.Data(
                predicate = predicate,
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
