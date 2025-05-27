package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class PutDataToContextTest : UnitTest() {

    init {

        "The extension function `put` for the `Context` type" - {

            "when the context is empty" - {
                val context = Context.empty()

                val value = JsonElement.Text(ORIGIN_VALUE)
                val result = context.put(SOURCE, value)

                "then function should be successful" {
                    result.shouldBeNone()
                }

                "then the context should contain the source" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the context should contain added value" {
                    val result = context.getOrNull(SOURCE)
                    result shouldBe value
                }
            }

            "when the context is not empty" - {

                "when the adding source is present in the context" - {
                    val value = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to value))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.put(SOURCE, newValue)

                    "then function should return an error" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<AddDataToContextErrors.SourceAlreadyExists>()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should contain the original value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe value
                    }
                }

                "when the adding source is missing in the context" - {
                    val value = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to value))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.put(NEW_SOURCE, newValue)

                    "then function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(NEW_SOURCE) shouldBe true
                    }

                    "then the context should contain the added value" {
                        val result = context.getOrNull(NEW_SOURCE)
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
