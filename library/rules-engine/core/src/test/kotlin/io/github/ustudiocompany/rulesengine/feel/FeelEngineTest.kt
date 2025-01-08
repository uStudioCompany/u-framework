package io.github.ustudiocompany.rulesengine.feel

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

internal class FeelEngineTest : UnitTest() {
    private val engine = FeelEngine(FeelEngineConfiguration(functionRegistry = emptyList()))

    init {

        "The FeelEngine type" - {

            "when the engine evaluates the bool expression" - {
                val expression = "2 > 1"
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by Decimal format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Bool>()
                    value.get shouldBe true
                }
            }

            "when the engine evaluates the text expression" - {

                "when the expression is a simple text" - {
                    val expression = """"$TEXT_VALUE_1" + " " + "$TEXT_VALUE_2""""
                    val variables = emptyMap<Source, DataElement>()
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Text type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                        value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                    }
                }
            }

            "when the engine evaluates the math expression" - {

                "when the result is a number" - {
                    val expression = "$NUMBER_VALUE_1 + $NUMBER_VALUE_2"
                    val variables = emptyMap<Source, DataElement>()
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Decimal type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }

                "when the result is a struct" - {
                    val expression = """{ "$KEY_A": $NUMBER_VALUE_1 + $NUMBER_VALUE_2 }"""
                    val variables = emptyMap<Source, DataElement>()
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Struct type" {
                        result.shouldBeSuccess()
                        val struct = result.value.shouldBeInstanceOf<DataElement.Struct>()
                        val value = struct[KEY_A].shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }

                "when the result is an array" - {
                    val expression = """[$NUMBER_VALUE_1 + $NUMBER_VALUE_2]"""
                    val variables = emptyMap<Source, DataElement>()
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Array type" {
                        result.shouldBeSuccess()
                        val array = result.value.shouldBeInstanceOf<DataElement.Array>()
                        array.size shouldBe 1
                        val value = array[0].shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }
            }

            "when the engine evaluates the array expression" - {
                val expression = """
                    | [
                    |   $NUMBER_VALUE_1,
                    |   "$TEXT_VALUE_1",
                    |   $BOOL_VALUE_TRUE, 
                    |   $BOOL_VALUE_FALSE,
                    |   $NULL_VALUE,
                    |   [
                    |     $NUMBER_VALUE_3,
                    |     $NUMBER_VALUE_4
                    |   ],
                    |   { 
                    |     "$KEY_A": $NUMBER_VALUE_5
                    |   }
                    | ]
                """.trimMargin()
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value as Array type" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Array>()
                    value shouldContainExactly listOf(
                        DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                        DataElement.Text(TEXT_VALUE_1),
                        DataElement.Bool(BOOL_VALUE_TRUE),
                        DataElement.Bool(BOOL_VALUE_FALSE),
                        DataElement.Null,
                        DataElement.Array(
                            mutableListOf(
                                DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_3)),
                                DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_4))
                            )
                        ),
                        DataElement.Struct(
                            mutableMapOf(
                                KEY_A to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_5))
                            )
                        )
                    )
                }
            }

            "when the engine evaluates the mixed expression" - {
                val expression = """
                    | [{a: 1}, {b: 2}].a
                """.trimMargin()
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value as Array type" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Array>()
                    value shouldContainExactly listOf(
                        DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                        DataElement.Null,
                    )
                }
            }

            "when the engine evaluates the struct expression" - {
                val expression = """
                    | {
                    |   "$KEY_A": $NUMBER_VALUE_1,
                    |   "$KEY_B": "$TEXT_VALUE_1",
                    |   "$KEY_C": $BOOL_VALUE_TRUE,
                    |   "$KEY_D": $BOOL_VALUE_FALSE,
                    |   "$KEY_E": $NULL_VALUE,
                    |   "$KEY_F": [$NUMBER_VALUE_3, $NUMBER_VALUE_4],
                    |   "$KEY_G": {
                    |     "$KEY_H": $NUMBER_VALUE_5
                    |   }
                    | }
                """.trimMargin()
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value as Struct type" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Struct>()
                    value shouldBe DataElement.Struct(
                        mutableMapOf(
                            KEY_A to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                            KEY_B to DataElement.Text(TEXT_VALUE_1),
                            KEY_C to DataElement.Bool(BOOL_VALUE_TRUE),
                            KEY_D to DataElement.Bool(BOOL_VALUE_FALSE),
                            KEY_E to DataElement.Null,
                            KEY_F to DataElement.Array(
                                mutableListOf(
                                    DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_3)),
                                    DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_4))
                                )
                            ),
                            KEY_G to DataElement.Struct(
                                mutableMapOf(
                                    KEY_H to DataElement.Decimal(
                                        BigDecimal.valueOf(NUMBER_VALUE_5)
                                    )
                                )
                            )
                        )
                    )
                }
            }

            "when the engine evaluates the expression with variables" - {

                "when the variable is a null value" - {
                    val expression = """ if $KEY_A = null then $BOOL_VALUE_TRUE else $BOOL_VALUE_FALSE """
                    val variables = mapOf(Source(KEY_A) to DataElement.Null)
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Bool type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Bool>()
                        value.get shouldBe BOOL_VALUE_TRUE
                    }
                }

                "when the variables are a boolean values" - {
                    val expression = "$KEY_A and $KEY_B"
                    val variables = mapOf(
                        Source(KEY_A) to DataElement.Bool(BOOL_VALUE_TRUE),
                        Source(KEY_B) to DataElement.Bool(BOOL_VALUE_FALSE)
                    )
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Bool type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Bool>()
                        value.get shouldBe false
                    }
                }

                "when the variables are a text values" - {
                    val expression = """$KEY_A + " " + $KEY_B"""
                    val variables = mapOf(
                        Source(KEY_A) to DataElement.Text(TEXT_VALUE_1),
                        Source(KEY_B) to DataElement.Text(TEXT_VALUE_2)
                    )
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Text type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                        value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                    }
                }

                "when the variables are a decimal values" - {
                    val expression = "$KEY_A + $KEY_B"
                    val variables = mapOf(
                        Source(KEY_A) to DataElement.Decimal(
                            BigDecimal.valueOf(NUMBER_VALUE_1)
                        ),
                        Source(KEY_B) to DataElement.Decimal(
                            BigDecimal.valueOf(NUMBER_VALUE_2)
                        )
                    )
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Decimal type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }

                "when the variable is an array value" - {
                    val expression = """sum($KEY_A)"""
                    val variables = mapOf(
                        Source(KEY_A) to DataElement.Array(
                            mutableListOf(
                                DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                            )
                        )
                    )
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Decimal type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }

                "when the variable is a struct value" - {
                    val expression = """$KEY_A.$KEY_B + $KEY_C.$KEY_D"""
                    val variables = mapOf(
                        Source(KEY_A) to DataElement.Struct(
                            mutableMapOf(KEY_B to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)))
                        ),
                        Source(KEY_C) to DataElement.Struct(
                            mutableMapOf(KEY_D to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2)))
                        )
                    )
                    val result = engine.parse(expression)
                        .andThen { expression -> engine.evaluate(expression, variables) }

                    "then the engine should return an value as Decimal type" {
                        result.shouldBeSuccess()
                        val value = result.value.shouldBeInstanceOf<DataElement.Decimal>()
                        value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                    }
                }
            }

            "when the engine evaluates the invalid expression" - {
                val expression = "1 ++ 1"
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a parsing error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Parsing>()
                }
            }

            "when the engine evaluates the expression with an unknown function" - {
                val expression = "1 + abc()"
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
                }
            }

            "when the engine evaluates the expression with assert" - {
                val expression = "assert(a, a != null)"
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return a evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
                }
            }
        }
    }

    private companion object {
        private const val NUMBER_VALUE_1 = 1L
        private const val NUMBER_VALUE_2 = 2L
        private const val NUMBER_VALUE_3 = 3L
        private const val NUMBER_VALUE_4 = 4L
        private const val NUMBER_VALUE_5 = 5L
        private const val TEXT_VALUE_1 = "Hello"
        private const val TEXT_VALUE_2 = "World"
        private const val BOOL_VALUE_TRUE = true
        private const val BOOL_VALUE_FALSE = false
        private val NULL_VALUE: Any? = null
        private const val KEY_A = "a"
        private const val KEY_B = "b"
        private const val KEY_C = "c"
        private const val KEY_D = "d"
        private const val KEY_E = "e"
        private const val KEY_F = "f"
        private const val KEY_G = "g"
        private const val KEY_H = "h"
    }
}
