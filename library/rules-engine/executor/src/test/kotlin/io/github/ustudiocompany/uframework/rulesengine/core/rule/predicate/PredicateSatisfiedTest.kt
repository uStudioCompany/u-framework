package io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.path.defaultPathCompiler
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator.EQ
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class PredicateSatisfiedTest : UnitTest() {

    init {

        "The extension function `isSatisfied` for the `Predicates` type" - {

            "when the predicates are null" - {
                val predicates: Predicates? = null

                "then the function should return the value true" {
                    val result = predicates.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the predicates are empty" - {
                val predicates = Predicates(emptyList())

                "then the function should return the value true" {
                    val result = predicates.isSatisfied(CONTEXT)
                    result shouldBeSuccess true
                }
            }

            "when the predicates are not empty" - {

                "when all predicate are satisfied" - {
                    val predicates = Predicates(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                compareWith = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                comparator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                compareWith = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                comparator = EQ
                            )
                        )
                    )

                    "then the function should return the value true" {
                        val result = predicates.isSatisfied(CONTEXT)
                        result shouldBeSuccess true
                    }
                }

                "when any predicate are not satisfied" - {
                    val predicates = Predicates(
                        listOf(
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_1)),
                                compareWith = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                comparator = EQ
                            ),
                            Predicate(
                                target = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                compareWith = Value.Literal(fact = DataElement.Text(VALUE_2)),
                                comparator = EQ
                            )
                        )
                    )

                    "then the function should return the value false" {
                        val result = predicates.isSatisfied(CONTEXT)
                        result shouldBeSuccess false
                    }
                }

                "when any predicate returns an error" - {
                    val predicates = Predicates(
                        listOf(
                            Predicate(
                                target = Value.Reference(source = SOURCE, path = PATH),
                                compareWith = Value.Literal(DataElement.Text(VALUE_1)),
                                comparator = EQ
                            )
                        )
                    )

                    "then function should return an error" {
                        val result = predicates.isSatisfied(CONTEXT)
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
