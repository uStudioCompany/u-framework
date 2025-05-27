package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.EQ
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class ValidationStepExecutorTest : UnitTest() {

    init {

        "The validation step executor" - {

            "when condition is missing" - {
                val condition: Condition = Condition.NONE

                "when execution of the step is successful" - {
                    val step = successfulStep(condition)

                    "then the executor should return a success result" {
                        val result = step.executeIfSatisfied(ENV_VARS, CONTEXT)
                        result.shouldContainSuccessInstance()
                            .shouldBeNull()
                    }
                }

                "when execution of the step is fail" - {
                    val step = failStep(condition)

                    "then the executor should return an error result" {
                        val result = step.executeIfSatisfied(ENV_VARS, CONTEXT)
                        val error = result.shouldContainSuccessInstance()
                            .shouldBeInstanceOf<ValidationStep.ErrorCode>()
                        error shouldBe ERROR_CODE
                    }
                }
            }

            "when condition is present" - {

                "when condition is satisfied" - {
                    val condition: Condition = satisfiedCondition()

                    "when execution of the step is successful" - {
                        val step = successfulStep(condition)

                        "then the executor should return a success result" {
                            val result = step.executeIfSatisfied(ENV_VARS, CONTEXT)
                            result.shouldContainSuccessInstance()
                                .shouldBeNull()
                        }
                    }

                    "when execution of the step is fail" - {
                        val step = failStep(condition)

                        "then the executor should return an error result" {
                            val result = step.executeIfSatisfied(ENV_VARS, CONTEXT)
                            val error = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<ValidationStep.ErrorCode>()
                            error shouldBe ERROR_CODE
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the step is not performed" - {
                        val step = failStep(condition)

                        val result = step.executeIfSatisfied(ENV_VARS, CONTEXT)
                        result.shouldContainSuccessInstance()
                            .shouldBeNull()
                    }
                }
            }
        }
    }

    private companion object {
        private val ENV_VARS = EnvVars.EMPTY
        private val CONTEXT = Context.empty()
        private val ERROR_CODE = ValidationStep.ErrorCode("err-1")
        private val TEXT_VALUE_1 = JsonElement.Text("value-1")
        private val TEXT_VALUE_2 = JsonElement.Text("value-2")

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

        private fun successfulStep(condition: Condition) =
            ValidationStep(
                condition = condition,
                target = Value.Literal(fact = TEXT_VALUE_1),
                value = Value.Literal(fact = TEXT_VALUE_1),
                operator = EQ,
                errorCode = ERROR_CODE
            )

        private fun failStep(condition: Condition) =
            ValidationStep(
                condition = condition,
                target = Value.Literal(fact = TEXT_VALUE_1),
                value = Value.Literal(fact = TEXT_VALUE_2),
                operator = EQ,
                errorCode = ERROR_CODE
            )
    }
}
