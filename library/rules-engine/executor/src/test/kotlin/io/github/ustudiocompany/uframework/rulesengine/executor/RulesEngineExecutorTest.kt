package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.RuleId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class RulesEngineExecutorTest : UnitTest() {

    init {
        "the RulesEngineExecutor" - {

            "test1" {
                val executor = RulesEngineExecutor(
                    dataProvider = { _, _ -> DATA_RETRIEVE_RESULT.asSuccess() },
                    messagePublisher = { _, _, _ -> Maybe.none() },
                    merger = { _, origin, _ -> origin.asSuccess() }
                )
                val envVars = envVarsOf()
                val context = Context.empty()

                val rules = Rules(
                    listOf(
                        Rule(
                            id = RULE_ID,
                            condition = Condition.NONE,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        id = STEP_ID,
                                        condition = Condition.NONE,
                                        target = Value.Literal(fact = JsonElement.Text("test")),
                                        value = Value.Literal(fact = JsonElement.Text("test")),
                                        operator = EQ,
                                        errorCode = ValidationStep.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(envVars, context, rules)
                result.shouldBeSuccess()
                result.value shouldBe null
            }

            "test2" {
                val executor = RulesEngineExecutor(
                    dataProvider = { _, _ -> DATA_RETRIEVE_RESULT.asSuccess() },
                    messagePublisher = { _, _, _ -> Maybe.none() },
                    merger = { _, origin, _ -> origin.asSuccess() }
                )
                val envVars = envVarsOf()
                val context = Context(sources = mapOf(Source("test") to JsonElement.Text("test")))
                val rules = Rules(
                    listOf(
                        Rule(
                            id = RULE_ID,
                            condition = Condition.NONE,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        id = STEP_ID,
                                        condition = Condition.NONE,
                                        target = Value.Literal(fact = JsonElement.Text("test")),
                                        value = Value.Literal(fact = JsonElement.Text("test2")),
                                        operator = EQ,
                                        errorCode = ValidationStep.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(envVars, context, rules)
                val error = result.shouldContainSuccessInstance()
                    .shouldBeInstanceOf<ValidationStep.ErrorCode>()
                error.get shouldBe "err-1"
            }
        }
    }

    private companion object {
        private val RULE_ID = RuleId("rule-1")
        private val STEP_ID = StepId("step-1")
        private val DATA_RETRIEVE_RESULT = JsonElement.Text("data")
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
