package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class FeelEngineTest : UnitTest() {
    private val engine = FeelEngine(FeelEngineConfiguration(functionRegistry = emptyList()))

    init {

        "The `FeelEngine` type" - {

            "when the engine parses the valid expression" - {

                "when the expression does not contain variables" - {
                    val variables = emptyMap<Source, DataElement>()

                    "when the expression is the bool" - {
                        val expression = shouldBeSuccess {
                            engine.parse("2 > 1")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value by Decimal format" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Bool>()
                            value.get shouldBe true
                        }
                    }

                    "when the expression is textual" - {

                        "when the expression is a simple text" - {
                            val expression = shouldBeSuccess {
                                engine.parse(""""$TEXT_VALUE_1" + " " + "$TEXT_VALUE_2"""")
                            }
                            val result = expression.evaluate(variables)

                            "then the engine should return an value as Text type" {
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<DataElement.Text>()
                                value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                            }
                        }
                    }

                    "when the expression is mathematical" - {

                        "when the result is a number" - {
                            val expression = shouldBeSuccess {
                                engine.parse("$NUMBER_VALUE_1 + $NUMBER_VALUE_2")
                            }
                            val result = expression.evaluate(variables)

                            "then the engine should return an value as Decimal type" {
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<DataElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }

                        "when the result is a struct" - {
                            val expression = shouldBeSuccess {
                                engine.parse("""{ "$KEY_A": $NUMBER_VALUE_1 + $NUMBER_VALUE_2 }""")
                            }
                            val result = expression.evaluate(variables)

                            "then the engine should return an value as Struct type" {
                                val struct = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<DataElement.Struct>()
                                val value = struct[KEY_A].shouldBeInstanceOf<DataElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }

                        "when the result is an array" - {
                            val expression = shouldBeSuccess {
                                engine.parse("""[$NUMBER_VALUE_1 + $NUMBER_VALUE_2]""")
                            }
                            val result = expression.evaluate(variables)

                            "then the engine should return an value as Array type" {
                                val array = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<DataElement.Array>()
                                array.size shouldBe 1
                                val value = array[0].shouldBeInstanceOf<DataElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }
                    }

                    "when the expression contains an array" - {
                        val expression = shouldBeSuccess {
                            engine.parse(
                                """
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
                            )
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Array type" {
                            val array = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Array>()
                            array shouldContainExactly listOf(
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

                    "when the expression contains a structure" - {
                        val expression = shouldBeSuccess {
                            engine.parse(
                                """
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
                            )
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Struct type" {
                            val struct = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Struct>()
                            struct shouldBe DataElement.Struct(
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

                    "when the expression is mixed" - {
                        val expression = shouldBeSuccess {
                            engine.parse("[{a: 1}, {b: 2}].a")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Array type" {
                            val array = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Array>()
                            array shouldContainExactly listOf(
                                DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                DataElement.Null,
                            )
                        }
                    }

                    "when the expression contains an unknown function" - {
                        val expression = shouldBeSuccess {
                            engine.parse("1 + abc()")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return a evaluation error" {
                            result.shouldContainFailureInstance()
                                .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                        }
                    }

                    "when the expression contains an assert" - {
                        val expression = shouldBeSuccess {
                            engine.parse("assert(a, a != null)")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return a evaluation error" {
                            result.shouldContainFailureInstance()
                                .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                        }
                    }
                }

                "when the expression contains variables" - {

                    "when the variable is a null value" - {
                        val variables = mapOf(Source(KEY_A) to DataElement.Null)
                        val expression = shouldBeSuccess {
                            engine.parse(
                                """ if $KEY_A = null then $BOOL_VALUE_TRUE else $BOOL_VALUE_FALSE """
                            )
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Bool>()
                            value.get shouldBe BOOL_VALUE_TRUE
                        }
                    }

                    "when the variables are a boolean values" - {
                        val variables = mapOf(
                            Source(KEY_A) to DataElement.Bool(BOOL_VALUE_TRUE),
                            Source(KEY_B) to DataElement.Bool(BOOL_VALUE_FALSE)
                        )
                        val expression = shouldBeSuccess {
                            engine.parse("$KEY_A and $KEY_B")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Bool>()
                            value.get shouldBe false
                        }
                    }

                    "when the variables are a text values" - {
                        val variables = mapOf(
                            Source(KEY_A) to DataElement.Text(TEXT_VALUE_1),
                            Source(KEY_B) to DataElement.Text(TEXT_VALUE_2)
                        )
                        val expression = shouldBeSuccess {
                            engine.parse("""$KEY_A + " " + $KEY_B""")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Text type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Text>()
                            value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                        }
                    }

                    "when the variables are a decimal values" - {
                        val variables = mapOf(
                            Source(KEY_A) to DataElement.Decimal(
                                BigDecimal.valueOf(NUMBER_VALUE_1)
                            ),
                            Source(KEY_B) to DataElement.Decimal(
                                BigDecimal.valueOf(NUMBER_VALUE_2)
                            )
                        )
                        val expression = shouldBeSuccess {
                            engine.parse("$KEY_A + $KEY_B")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is an array value" - {
                        val variables = mapOf(
                            Source(KEY_A) to DataElement.Array(
                                mutableListOf(
                                    DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                    DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                                )
                            )
                        )
                        val expression = shouldBeSuccess {
                            engine.parse("""sum($KEY_A)""")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is a struct value" - {
                        val variables = mapOf(
                            Source(KEY_A) to DataElement.Struct(
                                mutableMapOf(KEY_B to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)))
                            ),
                            Source(KEY_C) to DataElement.Struct(
                                mutableMapOf(KEY_D to DataElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2)))
                            )
                        )
                        val expression = shouldBeSuccess {
                            engine.parse("""$KEY_A.$KEY_B + $KEY_C.$KEY_D""")
                        }
                        val result = expression.evaluate(variables)

                        "then the engine should return an value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<DataElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }
                }
            }

            "when the engine parse the invalid expression" - {
                val result = engine.parse("1 ++ 1")

                "then the engine should return a parsing error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelEngine.Errors.Parsing>()
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
        private const val KEY_A = "Aa"
        private const val KEY_B = "Bb"
        private const val KEY_C = "Cc"
        private const val KEY_D = "Dd"
        private const val KEY_E = "Ee"
        private const val KEY_F = "Ff"
        private const val KEY_G = "Gg"
        private const val KEY_H = "Hh"
    }
}
