package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class RequirementStepExecutorTest : UnitTest() {

    init {

        "The requirement step executor" - {

            "when predicate is missing" - {
                val predicate: Predicates? = null

                "when execution of the step is successful" - {
                    val step = successfulStep(predicate)

                    "then the executor should return a success result" {
                        val result = step.execute(CONTEXT)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }

                "when execution of the step is fail" - {
                    val step = failStep(predicate)

                    "then the executor should return an error result" {
                        val result = step.execute(CONTEXT)
                        result.shouldBeSuccess()
                        val error = result.value.shouldBeInstanceOf<Step.ErrorCode>()
                        error shouldBe ERROR_CODE
                    }
                }
            }

            "when predicate is present" - {

                "when predicate is satisfied" - {
                    val predicate: Predicates = satisfiedPredicate()

                    "when execution of the step is successful" - {
                        val step = successfulStep(predicate)

                        "then the executor should return a success result" {
                            val result = step.execute(CONTEXT)
                            result.shouldBeSuccess()
                            result.value.shouldBeNull()
                        }
                    }

                    "when execution of the step is fail" - {
                        val step = failStep(predicate)

                        "then the executor should return an error result" {
                            val result = step.execute(CONTEXT)
                            result.shouldBeSuccess()
                            val error = result.value.shouldBeInstanceOf<Step.ErrorCode>()
                            error shouldBe ERROR_CODE
                        }
                    }
                }

                "when predicate is not satisfied" - {
                    val predicate: Predicates = notSatisfiedPredicate()

                    "then the step is not performed" - {
                        val step = failStep(predicate)

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

        private fun successfulStep(predicate: Predicates?) =
            Step.Requirement(
                predicate = predicate,
                target = Value.Literal(fact = TEXT_VALUE_1),
                compareWith = Value.Literal(fact = TEXT_VALUE_1),
                comparator = EQ,
                errorCode = ERROR_CODE
            )

        private fun failStep(predicate: Predicates?) =
            Step.Requirement(
                predicate = predicate,
                target = Value.Literal(fact = TEXT_VALUE_1),
                compareWith = Value.Literal(fact = TEXT_VALUE_2),
                comparator = EQ,
                errorCode = ERROR_CODE
            )
    }
}
