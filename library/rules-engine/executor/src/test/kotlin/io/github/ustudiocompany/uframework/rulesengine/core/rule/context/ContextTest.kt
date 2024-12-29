package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class ContextTest : UnitTest() {

    init {

        "The `Context` type" - {

            "when context does not contain the item" - {
                val context = Context.empty()

                "then the context should not contain the source" {
                    context.contains(SOURCE) shouldBe false
                }

                "then the context should return an error" {
                    val result = context[SOURCE]
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                }
            }

            "when adding a new item" - {
                val context = Context.empty()
                val value = DataElement.Text(ORIGIN_VALUE)
                val result = context.add(SOURCE, value)

                "then the operation should be successful" {
                    result.shouldBeSuccess()
                }

                "then the context should contain the source" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the context should return the value" {
                    val result = context[SOURCE]
                    result.shouldBeSuccess()
                    result.value shouldBe value
                }
            }

            "when adding already existing item" - {
                val origin = DataElement.Text(ORIGIN_VALUE)
                val context = Context(mapOf(SOURCE to origin))
                val newValue = DataElement.Text(NEW_VALUE)
                val result = context.add(SOURCE, newValue)

                "then the operation should return an error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<ContextError.SourceAlreadyExists>()
                }
            }

            "when replacing an item" - {

                "when the item is missing" - {
                    val context = Context.empty()
                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then the operation should return an error" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<ContextError.SourceMissing>()
                    }
                }

                "when the item is present" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))
                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then the operation should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context[SOURCE]
                        result.shouldBeSuccess()
                        result.value shouldBe newValue
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source")
        private const val ORIGIN_VALUE = "value-1"
        private const val NEW_VALUE = "value-2"
    }
}
