package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operators.EQ
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ValidationStepExecutorTest : UnitTest() {

    init {

        "The validation step executor" - {

            "when condition is missing" - {
                val condition: Condition? = null

                "when execution of the step is successful" - {
                    val step = successfulStep(condition)

                    "then the executor should return a success result" {
                        val result = step.execute(CONTEXT)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }

                "when execution of the step is fail" - {
                    val step = failStep(condition)

                    "then the executor should return an error result" {
                        val result = step.execute(CONTEXT)
                        result.shouldBeSuccess()
                        val error = result.value.shouldBeInstanceOf<Step.ErrorCode>()
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
                            val result = step.execute(CONTEXT)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }

                    "when execution of the step is fail" - {
                        val step = failStep(condition)

                        "then the executor should return an error result" {
                            val result = step.execute(CONTEXT)
                            result.shouldBeSuccess()
                            val error = result.value.shouldBeInstanceOf<Step.ErrorCode>()
                            error shouldBe ERROR_CODE
                        }
                    }
                }

                "when condition is not satisfied" - {
                    val condition: Condition = notSatisfiedCondition()

                    "then the step is not performed" - {
                        val step = failStep(condition)

                        val result = step.execute(CONTEXT)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }
            }
        }
    }

    private companion object {
        private val CONTEXT = Context.empty()
        private val ERROR_CODE = Step.ErrorCode("err-1")
        private val TEXT_VALUE_1 = DataElement.Text("value-1")
        private val TEXT_VALUE_2 = DataElement.Text("value-2")

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

        private fun successfulStep(condition: Condition?) =
            ValidationStep(
                condition = condition,
                target = Value.Literal(fact = TEXT_VALUE_1),
                value = Value.Literal(fact = TEXT_VALUE_1),
                operator = EQ,
                errorCode = ERROR_CODE
            )

        private fun failStep(condition: Condition?) =
            ValidationStep(
                condition = condition,
                target = Value.Literal(fact = TEXT_VALUE_1),
                value = Value.Literal(fact = TEXT_VALUE_2),
                operator = EQ,
                errorCode = ERROR_CODE
            )
    }
}
