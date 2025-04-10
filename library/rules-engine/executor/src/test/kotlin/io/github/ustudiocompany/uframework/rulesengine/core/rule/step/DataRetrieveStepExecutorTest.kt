package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataRetrieveStepExecutorTest : UnitTest() {

    init {

        "The data retrieve step executor" - {

            "when condition is missing" - {
                val condition: Condition? = null

                "when execution of the step is fail" - {

                    "when an external call error" - {
                        val step = DataRetrieveStep(
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(ID_PARAM_VALUE)
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.PUT
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.executeIfSatisfied(
                                context = CONTEXT,
                                dataProvider = { _, _ -> DataProvider.Errors.GetData().asFailure() },
                                merger = { origin, _ -> origin.asSuccess() }
                            )
                            result.shouldContainSomeInstance()
                                //TODO add other error
                                .shouldBeInstanceOf<DataRetrieveStepExecuteErrors.RetrievingExternalData>()
                        }
                    }

                    "when an error of merging" - {
                        val context = Context(mapOf(RESULT_SOURCE to DataElement.Text(ORIGIN_VALUE)))
                        val step = DataRetrieveStep(
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(ID_PARAM_VALUE)
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.MERGE
                            )
                        )

                        val result = step.executeIfSatisfied(
                            context = context,
                            dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                            merger = { _, _ -> Errors.TestMergerError.asFailure() }
                        )

                        "then the executor should return an error result" {
                            result.shouldContainSomeInstance()
                                //TODO add other error
                                .shouldBeInstanceOf<DataRetrieveStepExecuteErrors.UpdatingContext>()
                        }
                    }
                }

                "when execution of the step is successful" - {
                    val context = Context.empty()
                    val step = DataRetrieveStep(
                        condition = condition,
                        uri = Uri,
                        args = Args(
                            ID_PARAM_NAME to Value.Literal(
                                fact = DataElement.Text(ID_PARAM_VALUE)
                            )
                        ),
                        result = StepResult(
                            source = RESULT_SOURCE,
                            action = StepResult.Action.PUT
                        )
                    )

                    val result = step.executeIfSatisfied(
                        context = context,
                        dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                        merger = { origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeNone()
                    }

                    "then the context should be updated" {
                        val result = context[RESULT_SOURCE]
                        result shouldBe CALL_RESULT
                    }
                }
            }

            "when condition is present" - {

                "when condition is satisfied" - {
                    val condition: Condition = satisfiedCondition()

                    "when execution of the step is successful" - {
                        val context = Context.empty()
                        val step = DataRetrieveStep(
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                ID_PARAM_NAME to Value.Literal(
                                    fact = DataElement.Text(ID_PARAM_VALUE)
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.PUT
                            )
                        )

                        val result = step.executeIfSatisfied(
                            context = context,
                            dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                            merger = { origin, _ -> origin.asSuccess() }
                        )

                        "then the executor should return a success result" {
                            result.shouldBeNone()
                        }

                        "then the context should be updated" {
                            val result = context[RESULT_SOURCE]
                            result shouldBe CALL_RESULT
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the step is not performed" {
                        val step = DataRetrieveStep(
                            condition = condition,
                            uri = Uri,
                            args = Args(),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.PUT
                            )
                        )

                        val result = step.executeIfSatisfied(
                            context = CONTEXT,
                            dataProvider = { _, _ -> DataProvider.Errors.GetData().asFailure() },
                            merger = { _, _ -> Errors.TestMergerError.asFailure() }
                        )
                        result.shouldBeNone()
                    }
                }
            }
        }
    }

    private companion object {
        private val CONTEXT = Context.empty()
        private const val ORIGIN_VALUE = "origin"

        private val TEXT_VALUE_1 = DataElement.Text("value-1")
        private val TEXT_VALUE_2 = DataElement.Text("value-2")

        private val Uri = Uri("users:id")

        private const val ID_PARAM_NAME = "id"
        private const val ID_PARAM_VALUE = "1"

        private val RESULT_SOURCE = Source("output")

        private val CALL_RESULT = DataElement.Text("data")

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
    }

    private sealed interface Errors : Failure {

        data object TestDataProviderError : Errors {
            override val code: String = "DATA_PROVIDER_ERROR"
        }

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
