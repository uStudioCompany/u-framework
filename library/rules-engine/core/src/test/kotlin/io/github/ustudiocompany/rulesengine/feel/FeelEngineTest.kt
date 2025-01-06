package io.github.ustudiocompany.rulesengine.feel

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

internal class FeelEngineTest : UnitTest() {
    private val engine = FeelEngine(FeelEngineConfiguration())

    init {
        "The FeelEngine type" - {

            "when the engine evaluates the math expression" - {
                val expression = "1 + 1"
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Decimal>()
                    value.get shouldBe 2.toBigDecimal()
                }
            }

            "when the engine evaluates the bool expression" - {
                val expression = "2 > 1"
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Bool>()
                    value.get shouldBe true
                }
            }

            "when the engine evaluates the text expression" - {
                val expression = """"Hello" + " " + "World""""
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                    value.get shouldBe "Hello World"
                }
            }

            "when the engine evaluates the array expression" - {
                val expression = """[1, "hello", true, false, null, [4, 5], {"a":10}]"""
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Array>()
                    value shouldContainExactly listOf(
                        DataElement.Decimal(BigDecimal.valueOf(1)),
                        DataElement.Text("hello"),
                        DataElement.Bool(true),
                        DataElement.Bool(false),
                        DataElement.Null,
                        DataElement.Array(
                            mutableListOf(
                                DataElement.Decimal(BigDecimal.valueOf(4)),
                                DataElement.Decimal(BigDecimal.valueOf(5))
                            )
                        ),
                        DataElement.Struct(
                            mutableMapOf(
                                "a" to DataElement.Decimal(BigDecimal.valueOf(10))
                            )
                        )
                    )
                }
            }

            "when the engine evaluates the struct expression" - {
                val expression = """
                    | {
                    |   "a":1,
                    |   "b": "hello",
                    |   "c": true,
                    |   "d": false,
                    |   "e": null,
                    |   "f": [4, 5],
                    |   "g": {
                    |     "h": 10
                    |   }
                    | }
                """.trimMargin()
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Struct>()
                    value shouldBe DataElement.Struct(
                        mutableMapOf(
                            "a" to DataElement.Decimal(BigDecimal.valueOf(1)),
                            "b" to DataElement.Text("hello"),
                            "c" to DataElement.Bool(true),
                            "d" to DataElement.Bool(false),
                            "e" to DataElement.Null,
                            "f" to DataElement.Array(
                                mutableListOf(
                                    DataElement.Decimal(BigDecimal.valueOf(4)),
                                    DataElement.Decimal(BigDecimal.valueOf(5))
                                )
                            ),
                            "g" to DataElement.Struct(
                                mutableMapOf(
                                    "h" to DataElement.Decimal(BigDecimal.valueOf(10))
                                )
                            )
                        )
                    )
                }
            }

            "when the engine evaluates the invalid expression" - {
                val expression = "1 ++ 1"
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a parsing error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Parsing>()
                }
            }

            "when the engine evaluates the expression with an unknown function" - {
                val expression = "1 + abc()"
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
                }
            }

            "when the engine evaluates the expression with assert" - {
                val expression = "assert(a, a != null)"
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
                }
            }
        }
    }
}
