package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.fail.asException
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainErrorInstance
import io.github.airflux.commons.types.maybe.matcher.shouldContainExceptionInstance
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult.Action.Merge.StrategyCode
import io.github.ustudiocompany.uframework.rulesengine.executor.HttpCallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class HttpCallStepExecutorTest : UnitTest() {

    init {

        "The HTTP Call step executor" - {

            "when execution of the step is successful" - {

                "when args is missing" - {
                    val context = Context.empty()
                    val step = createStepWithoutArgs(StepResult.Action.Put)

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
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

                "when a body is missing" - {
                    val context = Context.empty()
                    val step = createStepWithoutBody(StepResult.Action.Put)

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
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

                "when a result is missing but response is present" - {
                    val context = Context.empty()
                    val step = createStepWithoutResult()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeNone()
                    }

                    "then the context should mot be updated" {
                        val result = context.getOrNull(RESULT_SOURCE)
                        result.shouldBeNull()
                    }
                }

                "when a result and a response is missing" - {
                    val context = Context.empty()
                    val step = createStepWithoutResult()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> Success.asNull },
                        merger = { _, origin, _ -> origin.asSuccess() }
                    )

                    "then the executor should return a success result" {
                        result.shouldBeNone()
                    }

                    "then the context should mot be updated" {
                        val result = context.getOrNull(RESULT_SOURCE)
                        result.shouldBeNull()
                    }
                }
            }

            "when execution of the step is fail" - {

                "when building the args is fail" - {
                    val step = createStepWithInvalidArgs(StepResult.Action.Put)

                    "then the executor should return an error result" - {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            httpCallProvider = { _, _, _ -> HttpCallProvider.Error().asError().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionErrors.ArgBuild>()
                    }
                }

                "when building the body is fail" - {
                    val step = createStepWithInvalidBody(StepResult.Action.Put)

                    "then the executor should return an error result" - {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            httpCallProvider = { _, _, _ -> HttpCallProvider.Error().asError().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionErrors.BodyBuild>()
                    }
                }

                "when an external call error" - {
                    val step = createSuccessStepWithFullInfo(StepResult.Action.Put)

                    "then the executor should return an error result" {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            httpCallProvider = { _, _, _ -> HttpCallProvider.Error().asError().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionErrors.Call>()
                    }
                }

                "when an external call incident" - {
                    val step = createSuccessStepWithFullInfo(StepResult.Action.Put)

                    "then the executor should return an incident" {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            httpCallProvider = { _, _, _ -> HttpCallProvider.Incident().asException().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainExceptionInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionIncident.Call>()
                    }
                }

                "when a result is present but a response missing " - {
                    val step = createSuccessStepWithFullInfo(StepResult.Action.Put)

                    "then the executor should return an error result" {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            httpCallProvider = { _, _, _ -> Success.asNull },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionErrors.ExpectedResponseMissing>()
                    }
                }

                "when an error of merging result" - {
                    val context = Context(sources = mapOf(RESULT_SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                    val step = createSuccessStepWithFullInfo(StepResult.Action.Merge(StrategyCode("")))

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
                        merger = { _, _, _ -> Merger.Error().asError().asFailure() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainErrorInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionErrors.ResultApply>()
                    }
                }

                "when an incident of merging result" - {
                    val context = Context(sources = mapOf(RESULT_SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                    val step = createSuccessStepWithFullInfo(StepResult.Action.Merge(MERGE_STRATEGY_CODE))

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        httpCallProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
                        merger = { _, _, _ -> Merger.Incident().asException().asFailure() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainExceptionInstance()
                            .shouldBeInstanceOf<HttpCallStepExecutionIncident.ResultApply>()
                    }
                }
            }
        }
    }

    private fun createSuccessStepWithFullInfo(action: StepResult.Action) = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = createBody(),
        result = createResult(action)
    )

    private fun createStepWithInvalidArgs(action: StepResult.Action) = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createInvalidArgs(),
        body = createBody(),
        result = createResult(action)
    )

    private fun createStepWithInvalidBody(action: StepResult.Action) = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = createInvalidBody(),
        result = createResult(action)
    )

    private fun createStepWithoutArgs(action: StepResult.Action) = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = Args.NONE,
        body = createBody(),
        result = createResult(action)
    )

    private fun createStepWithoutBody(action: StepResult.Action) = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = null,
        result = createResult(action)
    )

    private fun createStepWithoutResult() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = createBody(),
        result = null
    )

    private fun createArgs() = Args(
        listOf(
            Arg(
                name = ID_PARAM_NAME,
                value = Value.Literal(
                    fact = JsonElement.Text(ID_PARAM_VALUE)
                )
            )
        )
    )

    private fun createInvalidArgs() = Args(
        listOf(
            Arg(
                name = ID_PARAM_NAME,
                value = Value.Reference(
                    source = INVALID_ARG_SOURCE,
                    path = path()
                )
            )
        )
    )

    private fun createBody() = Value.Literal(fact = JsonElement.Text(ID_DATA_VALUE))

    private fun createInvalidBody() = Value.Reference(source = INVALID_ARG_SOURCE, path = path())

    private fun createResult(action: StepResult.Action) = StepResult(
        source = RESULT_SOURCE,
        action = action
    )

    private companion object {
        private val STEP_ID = StepId("step-1")
        private val ENV_VARS = envVarsOf()
        private val CONTEXT = Context.empty()
        private const val ORIGIN_VALUE = "origin"
        private val Uri = Uri("users:id")

        private const val ID_PARAM_NAME = "id"
        private const val ID_PARAM_VALUE = "1"

        private const val ID_DATA_VALUE = "0000-0000-0000-0000"

        private val INVALID_ARG_SOURCE = Source("INVALID_SOURCE")
        private val RESULT_SOURCE = Source("output")

        private val CALL_RESULT = JsonElement.Text("data")
        private const val PATH_VALUE = "$.id"

        private val MERGE_STRATEGY_CODE = StrategyCode("merge-strategy-code")

        private fun path() =
            object : Path {
                override val text: String = PATH_VALUE

                override fun searchIn(data: JsonElement): ResultK<JsonElement?, Path.SearchError> =
                    ResultK.Success.asNull
            }
    }
}
