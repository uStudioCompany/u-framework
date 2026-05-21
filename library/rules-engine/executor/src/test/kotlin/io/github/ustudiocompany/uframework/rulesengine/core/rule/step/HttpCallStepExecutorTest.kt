package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
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
                    val step = createStepWithoutArgs()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        callProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
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

                "when body is missing" - {
                    val context = Context.empty()
                    val step = createStepWithoutBody()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        callProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
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

                "when result is missing" - {
                    val context = Context.empty()
                    val step = createStepWithoutResult()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        callProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
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
                    val step = createStepWithInvalidArgs()

                    "then the executor should return an error result" - {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            callProvider = { _, _, _ -> CallProvider.Error().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<HttpCallStepExecuteErrors.ArgsBuilding>()
                    }
                }

                "when building the body is fail" - {
                    val step = createStepWithInvalidBody()

                    "then the executor should return an error result" - {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            callProvider = { _, _, _ -> CallProvider.Error().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<HttpCallStepExecuteErrors.BodyBuilding>()
                    }
                }

                "when an external call error" - {
                    val step = createSuccessStepWithFullInfo()

                    "then the executor should return an error result" {
                        val result = step.execute(
                            envVars = ENV_VARS,
                            context = CONTEXT,
                            callProvider = { _, _, _ -> CallProvider.Error().asFailure() },
                            merger = { _, origin, _ -> origin.asSuccess() }
                        )
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<HttpCallStepExecuteErrors.Call>()
                    }
                }

                "when an error of merging" - {
                    val context = Context(sources = mapOf(RESULT_SOURCE to JsonElement.Text(ORIGIN_VALUE)))
                    val step = createSuccessStepWithFullInfo()

                    val result = step.execute(
                        envVars = ENV_VARS,
                        context = context,
                        callProvider = { _, _, _ -> CALL_RESULT.asSuccess() },
                        merger = { _, _, _ -> Merger.Error().asFailure() }
                    )

                    "then the executor should return an error result" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<HttpCallStepExecuteErrors.UpdatingContext>()
                    }
                }
            }
        }
    }

    private fun createSuccessStepWithFullInfo() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = createBody(),
        result = createResult()
    )

    private fun createStepWithInvalidArgs() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createInvalidArgs(),
        body = createBody(),
        result = createResult()
    )

    private fun createStepWithInvalidBody() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = createInvalidBody(),
        result = createResult()
    )

    private fun createStepWithoutArgs() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = Args.NONE,
        body = createBody(),
        result = createResult()
    )

    private fun createStepWithoutBody() = HttpCallStep(
        id = STEP_ID,
        condition = Condition.NONE,
        uri = Uri,
        args = createArgs(),
        body = null,
        result = createResult()
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

    private fun createResult() = StepResult(
        source = RESULT_SOURCE,
        action = StepResult.Action.Put
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

        private fun path() =
            object : Path {
                override val text: String = PATH_VALUE

                override fun searchIn(data: JsonElement): ResultK<JsonElement?, Path.SearchError> =
                    ResultK.Success.asNull
            }
    }
}
