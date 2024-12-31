package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operators.EQ
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ConditionSatisfiedTest : UnitTest() {

    init {

        "The extension function `isSatisfied` for the `Condition` type" - {

            "when the condition is null" - {
                val condition: Condition? = null

                "then the function should return the value true" {
                    val result = condition.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the condition is empty" - {
                val condition = Condition(emptyList())

                "then the function should return the value true" {
                    val result = condition.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the condition is not empty" - {

                "when all predicate are satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value true" {
                        val result = condition.isSatisfied(CONTEXT)
                        result shouldBeSuccess true
                    }
                }

                "when any predicate are not satisfied" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                value = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                operator = EQ
                            )
                        )
                    )

                    "then the function should return the value false" {
                        val result = condition.isSatisfied(CONTEXT)
                        result shouldBeSuccess false
                    }
                }

                "when any predicate returns an error" - {
                    val condition = Condition(
                        listOf(
                            Predicate(
                                target = Value.Reference(source = SOURCE, path = PATH),
                                value = Value.Literal(DataElement.Text(VALUE_1)),
                                operator = EQ
                            )
                        )
                    )

                    "then function should return an error" {
                        val result = condition.isSatisfied(CONTEXT)
                        result.shouldBeFailure()
                        val error = result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                        error.source shouldBe SOURCE
                    }
                }
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()
        private val PATH_COMPILER = defaultPathCompiler(ObjectMapper())

        private const val SOURCE_NAME = "input.body"
        private val SOURCE = Source(SOURCE_NAME)

        private const val VALUE_1 = "value-1"
        private const val VALUE_2 = "value-2"

        private val PATH = "$.id".compile()

        private fun String.compile(): Path = PATH_COMPILER.compile(this).orThrow { error(it.description) }
    }
}
