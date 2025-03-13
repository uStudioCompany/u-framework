package io.github.ustudiocompany.rulesengine.core.context

import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ContextTest : UnitTest() {

    init {

        "The `Context` type" - {

            "when context is empty" - {
                val context = Context.empty()

                "then the function `contains` should return false" {
                    context.contains(SOURCE) shouldBe false
                }

                "then the function `get` should return null" {
                    val result = context[SOURCE]
                    result.shouldBeNull()
                }
            }

            "when context is not empty" - {
                val origin = DataElement.Text(ORIGIN_VALUE)
                val source = SOURCE
                val context = Context(mapOf(source to origin))

                "then the function `contains` for existing source should return true" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the function `contains` for non-existing source should return false" {
                    context.contains(NEW_SOURCE) shouldBe false
                }

                "then the function `get` for existing source should return the value" {
                    val result = context[SOURCE]
                    result shouldBe origin
                }

                "then the function `get` for non-existing source should return the null value" {
                    val result = context[NEW_SOURCE]
                    result.shouldBeNull()
                }
            }

            "when adding source" - {

                "when the source is missing in context" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.add(NEW_SOURCE, newValue)

                    "then the operation should return true" {
                        result shouldBe true
                    }

                    "then the context should contain the source" {
                        context.contains(NEW_SOURCE) shouldBe true
                    }

                    "then the context should contain the added value" {
                        val result = context[NEW_SOURCE]
                        result shouldBe newValue
                    }
                }

                "when the source is present in context" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.add(SOURCE, newValue)

                    "then the operation should return false" {
                        result shouldBe false
                    }

                    "then the function `contains` should return true" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the function `get` should return the original value" {
                        val result = context[SOURCE]
                        result shouldBe origin
                    }
                }
            }

            "when replacing source" - {

                "when the source is missing in context" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val source = SOURCE
                    val context = Context(mapOf(source to origin))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(NEW_SOURCE, newValue)

                    "then the operation should return false" {
                        result shouldBe false
                    }

                    ""
                }

                "when the source is present in context" - {
                    val origin = DataElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))

                    val newValue = DataElement.Text(NEW_VALUE)
                    val result = context.replace(SOURCE, newValue)

                    "then the operation should return true" {
                        result shouldBe true
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context[SOURCE]
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
}
