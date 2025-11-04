package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataRetrieveStepExecutorTest : UnitTest() {

    init {

        "The data retrieve step executor" - {

            "when condition is missing" - {
                val condition: Condition = Condition.NONE

                "when execution of the step is fail" - {

                    "when an external call error" - {
                        val step = DataRetrieveStep(
                            id = STEP_ID,
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                listOf(
                                    Arg(
                                        name = ID_PARAM_NAME,
                                        value = Value.Literal(
                                            fact = JsonElement.Text(ID_PARAM_VALUE)
                                        )
                                    )
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.Put
                            )
                        )

                        "then the executor should return an error result" {
                            val result = step.executeIfSatisfied(
                                envVars = ENV_VARS,
                                context = CONTEXT,
                                dataProvider = { _, _ -> DataProvider.Error().asFailure() },
                                merger = { _, origin, _ -> origin.asSuccess() }
                            )
                            result.shouldContainSomeInstance()
                                .shouldBeInstanceOf<DataRetrieveStepExecuteErrors.RetrievingExternalData>()
                        }
                    }

                    "when an error of merging" - {
                        val context = Context(sources = mapOf(RESULT_SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                        val step = DataRetrieveStep(
                            id = STEP_ID,
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                listOf(
                                    Arg(
                                        name = ID_PARAM_NAME,
                                        value = Value.Literal(
                                            fact = JsonElement.Text(ID_PARAM_VALUE)
                                        )
                                    )
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.Merge(strategyCode = MERGE_STRATEGY_CODE)
                            )
                        )

                        val result = step.executeIfSatisfied(
                            envVars = ENV_VARS,
                            context = context,
                            dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                            merger = { _, _, _ -> Merger.Error().asFailure() }
                        )

                        "then the executor should return an error result" {
                            result.shouldContainSomeInstance()
                                .shouldBeInstanceOf<DataRetrieveStepExecuteErrors.UpdatingContext>()
                        }
                    }
                }

                "when execution of the step is successful" - {
                    val context = Context.empty()
                    val step = DataRetrieveStep(
                        id = STEP_ID,
                        condition = condition,
                        uri = Uri,
                        args = Args(
                            listOf(
                                Arg(
                                    name = ID_PARAM_NAME,
                                    value = Value.Literal(
                                        fact = JsonElement.Text(ID_PARAM_VALUE)
                                    )
                                )
                            )
                        ),
                        result = StepResult(
                            source = RESULT_SOURCE,
                            action = StepResult.Action.Put
                        )
                    )

                    val result = step.executeIfSatisfied(
                        envVars = ENV_VARS,
                        context = context,
                        dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeNone()
                    }

                    "then the context should be updated" {
                        val result = context.getOrNull(RESULT_SOURCE)
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
                            id = STEP_ID,
                            condition = condition,
                            uri = Uri,
                            args = Args(
                                listOf(
                                    Arg(
                                        name = ID_PARAM_NAME,
                                        value = Value.Literal(
                                            fact = JsonElement.Text(ID_PARAM_VALUE)
                                        )
                                    )
                                )
                            ),
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.Put
                            )
                        )

                        val result = step.executeIfSatisfied(
                            envVars = ENV_VARS,
                            context = context,
                            dataProvider = { _, _ -> CALL_RESULT.asSuccess() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )

                        "then the executor should return a success result" {
                            result.shouldBeNone()
                        }

                        "then the context should be updated" {
                            val result = context.getOrNull(RESULT_SOURCE)
                            result shouldBe CALL_RESULT
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the step is not performed" {
                        val step = DataRetrieveStep(
                            id = STEP_ID,
                            condition = condition,
                            uri = Uri,
                            args = Args.NONE,
                            result = StepResult(
                                source = RESULT_SOURCE,
                                action = StepResult.Action.Put
                            )
                        )

                        val result = step.executeIfSatisfied(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            dataProvider = { _, _ -> DataProvider.Error().asFailure() },
                            merger = { _, _, _ -> Merger.Error().asFailure() }
                        )
                        result.shouldBeNone()
                    }
                }
            }
        }
    }

    private companion object {
        private val STEP_ID = StepId("step-1")
        private val ENV_VARS = envVarsOf()
        private val CONTEXT = Context.empty()
        private const val ORIGIN_VALUE = "origin"

        private val TEXT_VALUE_1 = JsonElement.Text("value-1")
        private val TEXT_VALUE_2 = JsonElement.Text("value-2")

        private val Uri = Uri("users:id")

        private const val ID_PARAM_NAME = "id"
        private const val ID_PARAM_VALUE = "1"

        private val RESULT_SOURCE = Source("output")

        private val CALL_RESULT = JsonElement.Text("data")
        private val MERGE_STRATEGY_CODE = StepResult.Action.Merge.StrategyCode("merge-strategy-code")

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
}
