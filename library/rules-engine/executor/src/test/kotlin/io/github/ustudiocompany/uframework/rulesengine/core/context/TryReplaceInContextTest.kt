package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.maybe.matcher.shouldBeNone
import io.github.airflux.commons.types.maybe.matcher.shouldContainSomeInstance
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class TryReplaceInContextTest : UnitTest() {

    init {

        "The extension function `tryReplace` for `Context` type" - {

            "when context is empty" - {
                val context = Context.empty()

                val value = DataElement.Text(ORIGIN_VALUE)
                val result = context.tryReplace(SOURCE, value)

                "then function should return an error" {
                    result.shouldContainSomeInstance()
                        .shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }

            "when context is not empty" - {

                "when replacing source is present in context" - {
                    val value = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to value))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.tryReplace(SOURCE, newValue)

                    "then function should be successful" {
                        result.shouldBeNone()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should contain the new value" {
                        val result = context[SOURCE]
                        result shouldBe newValue
                    }
                }

                "when replacing source is missing in context" - {
                    val value = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to value))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.tryReplace(UNKNOWN_SOURCE, newValue)

                    "then function should return an error" {
                        result.shouldContainSomeInstance()
                            .shouldBeInstanceOf<ContextError.SourceMissing>()
                    }

                    "then the context should not contain the source" {
                        context.contains(UNKNOWN_SOURCE) shouldBe false
                    }

                    "then the context should not contain the new value" {
                        val result = context[UNKNOWN_SOURCE]
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
