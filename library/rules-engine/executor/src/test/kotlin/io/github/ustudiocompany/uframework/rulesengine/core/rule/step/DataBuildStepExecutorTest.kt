package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.fail.asException
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainErrorInstance
import io.github.airflux.commons.types.maybe.matcher.shouldContainExceptionInstance
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult.Action.Merge.StrategyCode
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataBuildStepExecutorTest : UnitTest() {

    init {

        "The data step executor" - {

            "when execution of the step is successful" - {

                "then the executor should perform the step" - {
                    val step = createStep(StepResult.Action.Put)
                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = CONTEXT,
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the generated data" {
                        val result = CONTEXT.getOrNull(SOURCE)
                        result shouldBe EXPECTED_DATA
                    }
                }
            }

            "when execution of the step is fail" - {

                "when an error of building data by scheme" - {
                    val step = DataBuildStep(
                        id = STEP_ID,
                        condition = Condition.NONE,
                        dataSchema = DataSchema.Struct(
                            properties = listOf(
                                DataSchema.Property.Element(
                                    name = RESULT_ID_DATA_KEY,
                                    value = Value.EnvVars(name = UNKNOW_ENV_VAR)
                                )
                            )
                        ),
                        result = StepResult(
                            source = SOURCE,
                            action = StepResult.Action.Put
                        )
                    )
                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = CONTEXT,
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<DataBuildStepExecutionErrors.DataBuild>()
                    }
                }

                "when an error of putting result" - {
                    val context = Context(SOURCE to ACTUAL_DATA)
                    val step = createStep(StepResult.Action.Put)
                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<DataBuildStepExecutionErrors.ResultApply>()
                    }
                }

                "when an error of replacing result" - {
                    val step = createStep(StepResult.Action.Put)
                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = CONTEXT,
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<DataBuildStepExecutionErrors.ResultApply>()
                    }
                }

                "when an error of merging result" - {
                    val step = createStep(StepResult.Action.Merge(MERGE_STRATEGY_CODE))
                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = CONTEXT,
                        merger = { _, _, _ -> Merger.Error().asError().asFailure() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<DataBuildStepExecutionErrors.ResultApply>()
                    }
                }

                "when an incident of merging result" - {
                    val step = createStep(StepResult.Action.Merge(MERGE_STRATEGY_CODE))

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = CONTEXT,
                        merger = { _, _, _ -> Merger.Incident().asException().asFailure() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainExceptionInstance()
                            .shouldBeInstanceOf<DataBuildStepExecutionIncident.ResultApply>()
                    }
                }
            }
        }
    }

    private companion object {
        private val ENV_VARS = envVarsOf()
        private val CONTEXT = Context.empty()
        private val STEP_ID = StepId("step-1")
        private const val ID_DATA_KEY = "code"
        private const val ID_DATA_VALUE = "1111-1111-1111-1111"
        private const val RESULT_ID_DATA_KEY = "id"
        private const val RESULT_ID_DATA_VALUE = "0000-0000-0000-0000"
        private val SOURCE = Source("output")
        private val MERGE_STRATEGY_CODE = StrategyCode("merge-strategy-code")
        private val ACTUAL_DATA = JsonElement.Struct(ID_DATA_KEY to JsonElement.Text(ID_DATA_VALUE))
        private val EXPECTED_DATA = JsonElement.Struct(RESULT_ID_DATA_KEY to JsonElement.Text(RESULT_ID_DATA_VALUE))
        private val UNKNOW_ENV_VAR = EnvVarName("unknow")

        private fun createStep(action: StepResult.Action) =
            DataBuildStep(
                id = STEP_ID,
                condition = Condition.NONE,
                dataSchema = DataSchema.Struct(
                    properties = listOf(
                        DataSchema.Property.Element(
                            name = RESULT_ID_DATA_KEY,
                            value = Value.Literal(fact = JsonElement.Text(RESULT_ID_DATA_VALUE))
                        )
                    )
                ),
                result = StepResult(
                    source = SOURCE,
                    action = action
                )
            )
    }
}
