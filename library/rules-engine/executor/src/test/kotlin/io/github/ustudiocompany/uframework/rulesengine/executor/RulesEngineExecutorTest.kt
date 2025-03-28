package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
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
                    callProvider = { _, _ -> CALL_RESULT.asSuccess() },
                    merger = { origin, _ -> origin.asSuccess() }
                )
                val context = Context.empty()

                val rules = Rules(
                    listOf(
                        Rule(
                            condition = null,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        condition = null,
                                        target = Value.Literal(fact = DataElement.Text("test")),
                                        value = Value.Literal(fact = DataElement.Text("test")),
                                        operator = EQ,
                                        errorCode = ValidationStep.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                result.shouldBeSuccess()
                result.value shouldBe null
            }

            "test2" {
                val executor = RulesEngineExecutor(
                    callProvider = { _, _ -> CALL_RESULT.asSuccess() },
                    merger = { origin, _ -> origin.asSuccess() }
                )
                val context = Context(mapOf(Source("test") to DataElement.Text("test")))
                val rules = Rules(
                    listOf(
                        Rule(
                            condition = null,
                            steps = Steps(
                                listOf(
                                    ValidationStep(
                                        condition = null,
                                        target = Value.Literal(fact = DataElement.Text("test")),
                                        value = Value.Literal(fact = DataElement.Text("test2")),
                                        operator = EQ,
                                        errorCode = ValidationStep.ErrorCode("err-1")
                                    )
                                )
                            )
                        )
                    )
                )

                val result = executor.execute(context, rules)
                val error = result.shouldContainSuccessInstance()
                    .shouldBeInstanceOf<ValidationStep.ErrorCode>()
                error.get shouldBe "err-1"
            }
        }
    }

    private companion object {
        private val CALL_RESULT = DataElement.Text("data")
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
