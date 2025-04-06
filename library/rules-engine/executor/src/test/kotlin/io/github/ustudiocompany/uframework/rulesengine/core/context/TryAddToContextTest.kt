package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class TryAddToContextTest : UnitTest() {

    init {

        "The extension function `tryAdd` for `Context` type" - {

            "when context is empty" - {
                val context = Context.empty()

                val value = DataElement.Text(ORIGIN_VALUE)
                val result = context.tryAdd(SOURCE, value)

                "then function should be successful" {
                    result.shouldBeNone()
                }

                "then the context should contain the source" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the context should contain added value" {
                    val result = context[SOURCE]
                    result shouldBe value
                }
            }

            "when context is not empty" - {

                "when adding source is present in context" - {
                    val value = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to value))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.tryAdd(SOURCE, newValue)

                    "then function should return an error" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<AddDataToContextErrors.SourceAlreadyExists>()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should contain the original value" {
                        val result = context[SOURCE]
                        result shouldBe value
                    }
                }

                "when adding source is missing in context" - {
                    val value = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to value))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.tryAdd(NEW_SOURCE, newValue)

                    "then function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(NEW_SOURCE) shouldBe true
                    }

                    "then the context should contain the added value" {
                        val result = context[NEW_SOURCE]
                        result shouldBe newValue
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source-1")
        private val NEW_SOURCE = Source("source-2")
        private const val ORIGIN_VALUE = "value-1"
        private const val NEW_VALUE = "value-2"
    }

    private sealed interface Errors : Failure {

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
