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
internal class ReplaceDataInContextTest : UnitTest() {

    init {

        "The extension function `replace` for the `Context` type" - {

            "when the context is empty" - {
                val context = Context.empty()

                val value = JsonElement.Text(ORIGIN_VALUE)
                val result = context.replace(SOURCE, value)

                "then function should return an error" {
                    result.shouldContainSomeInstance()
                        .shouldBeInstanceOf<ReplaceDataInContextErrors.SourceMissing>()
                }
            }

            "when the context is not empty" - {

                "when the replacing source is present in the context" - {
                    val value = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to value))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should contain the new value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe newValue
                    }
                }

                "when the replacing source is missing in the context" - {
                    val value = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to value))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.replace(UNKNOWN_SOURCE, newValue)

                    "then function should return an error" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<ReplaceDataInContextErrors.SourceMissing>()
                    }

                    "then the context should not contain the source" {
                        context.contains(UNKNOWN_SOURCE) shouldBe false
                    }

                    "then the context should not contain the new value" {
                        val result = context.getOrNull(UNKNOWN_SOURCE)
                        result shouldBe null
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source-1")
        private val UNKNOWN_SOURCE = Source("source-2")
        private const val ORIGIN_VALUE = "value-1"
        private const val NEW_VALUE = "value-2"
    }

    private sealed interface Errors : Failure {

        data object TestMergerError : Errors {
            override val code: String = "MERGER_ERROR"
        }
    }
}
