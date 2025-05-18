package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class ContextTest : UnitTest() {

    init {

        "The `Context` type" - {

            "when the context is empty" - {
                val context = Context.empty()

                "then the function `isEmpty` source should return true" {
                    context.isEmpty() shouldBe true
                }

                "then the function `isNotEmpty` source should return false" {
                    context.isNotEmpty() shouldBe false
                }

                "then the function `contains` should return false" {
                    context.contains(SOURCE) shouldBe false
                }

                "then the function `getOrNull` should return null" {
                    val result = context.getOrNull(SOURCE)
                    result.shouldBeNull()
                }
            }

            "when the context is not empty" - {
                val origin = JsonElement.Text(ORIGIN_VALUE)
                val source = SOURCE
                val context = Context(sources = mapOf(source to origin))

                "then the function `isEmpty` source should return false" {
                    context.isEmpty() shouldBe false
                }

                "then the function `isNotEmpty` source should return true" {
                    context.isNotEmpty() shouldBe true
                }

                "then the function `contains` for existing source should return true" {
                    context.contains(SOURCE) shouldBe true
                }

                "then the function `contains` for non-existing source should return false" {
                    context.contains(UNKNOWN_SOURCE) shouldBe false
                }

                "then the function `getOrNull` for existing source should return the value" {
                    val result = context.getOrNull(SOURCE)
                    result shouldBe origin
                }

                "then the function `getOrNull` for non-existing source should return the null value" {
                    val result = context.getOrNull(UNKNOWN_SOURCE)
                    result.shouldBeNull()
                }
            }

            "when the adding source to the context" - {

                "when the source is missing in the context" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to origin))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.putIfAbsent(NEW_SOURCE, newValue)

                    "then the operation should return true" {
                        result shouldBe true
                    }

                    "then the context should contain the source" {
                        context.contains(NEW_SOURCE) shouldBe true
                    }

                    "then the context should contain the added value" {
                        val result = context.getOrNull(NEW_SOURCE)
                        result shouldBe newValue
                    }
                }

                "when the source is present in the context" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to origin))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.putIfAbsent(SOURCE, newValue)

                    "then the operation should return false" {
                        result shouldBe false
                    }

                    "then the function `contains` should return true" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the function `getOrNull` should return the original value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe origin
                    }
                }
            }

            "when the replacing source in the context" - {

                "when the source is missing in the context" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val source = SOURCE
                    val context = Context(sources = mapOf(source to origin))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.putIfPresent(NEW_SOURCE, newValue)

                    "then the operation should return false" {
                        result shouldBe false
                    }
                }

                "when the source is present in the context" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(sources = mapOf(SOURCE to origin))

                    val newValue = JsonElement.Text(NEW_VALUE)
                    val result = context.putIfPresent(SOURCE, newValue)

                    "then the operation should return true" {
                        result shouldBe true
                    }

                    "then the context should contain the source" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the context should return the new value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe newValue
                    }
                }
            }

            "when the context created from pairs" - {

                "when any pair is not passed" - {
                    val context = Context()

                    "then the function `isEmpty` source should return true" {
                        context.isEmpty() shouldBe true
                    }

                    "then the function `isNotEmpty` source should return false" {
                        context.isNotEmpty() shouldBe false
                    }

                    "then the function `contains` should return false" {
                        context.contains(SOURCE) shouldBe false
                    }

                    "then the function `getOrNull` should return null" {
                        val result = context.getOrNull(SOURCE)
                        result.shouldBeNull()
                    }
                }

                "when some pair is passed" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(SOURCE to origin)

                    "then the function `isEmpty` source should return false" {
                        context.isEmpty() shouldBe false
                    }

                    "then the function `isNotEmpty` source should return true" {
                        context.isNotEmpty() shouldBe true
                    }

                    "then the function `contains` for existing source should return true" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the function `contains` for non-existing source should return false" {
                        context.contains(UNKNOWN_SOURCE) shouldBe false
                    }

                    "then the function `getOrNull` for existing source should return the value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe origin
                    }

                    "then the function `getOrNull` for non-existing source should return the null value" {
                        val result = context.getOrNull(UNKNOWN_SOURCE)
                        result.shouldBeNull()
                    }

                    "then the variables property should contain the source" {
                        context shouldContainExactly listOf(SOURCE to origin)
                    }
                }
            }

            "when the context created from a map" - {

                "when the map is empty" - {
                    val context = Context(emptyMap())

                    "then the function `isEmpty` source should return true" {
                        context.isEmpty() shouldBe true
                    }

                    "then the function `isNotEmpty` source should return false" {
                        context.isNotEmpty() shouldBe false
                    }

                    "then the function `contains` should return false" {
                        context.contains(SOURCE) shouldBe false
                    }

                    "then the function `getOrNull` should return null" {
                        val result = context.getOrNull(SOURCE)
                        result.shouldBeNull()
                    }
                }

                "when the map is not empty" - {
                    val origin = JsonElement.Text(ORIGIN_VALUE)
                    val context = Context(mapOf(SOURCE to origin))

                    "then the function `isEmpty` source should return false" {
                        context.isEmpty() shouldBe false
                    }

                    "then the function `isNotEmpty` source should return true" {
                        context.isNotEmpty() shouldBe true
                    }

                    "then the function `contains` for existing source should return true" {
                        context.contains(SOURCE) shouldBe true
                    }

                    "then the function `contains` for non-existing source should return false" {
                        context.contains(UNKNOWN_SOURCE) shouldBe false
                    }

                    "then the function `getOrNull` for existing source should return the value" {
                        val result = context.getOrNull(SOURCE)
                        result shouldBe origin
                    }

                    "then the function `getOrNull` for non-existing source should return the null value" {
                        val result = context.getOrNull(UNKNOWN_SOURCE)
                        result.shouldBeNull()
                    }

                    "then the variables property should contain the source" {
                        context shouldContainExactly listOf(SOURCE to origin)
                    }
                }
            }
        }
    }

    private companion object {
        private val SOURCE = Source("source_1")
        private val NEW_SOURCE = Source("source_2")
        private val UNKNOWN_SOURCE = Source("source_3")
        private const val ORIGIN_VALUE = "value-1"
        private const val NEW_VALUE = "value-2"
    }
}
