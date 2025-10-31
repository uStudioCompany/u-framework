package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsMapOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class FeelExpressionParserTest : UnitTest() {

    init {

        "The `FeelExpressionParser` type" - {

            "when parsing a valid expression" - {

                "when the expression does not contain variables" - {
                    val envVars = envVarsMapOf()
                    val context = Context.empty()

                    "when the expression is the bool" - {
                        val expression = shouldBeSuccess {
                            parser.parse("2 > 1")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value by Decimal format" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Bool>()
                            value.get shouldBe true
                        }
                    }

                    "when the expression is textual" - {

                        "when the expression is a simple text" - {
                            val expression = shouldBeSuccess {
                                parser.parse(""""$TEXT_VALUE_1" + " " + "$TEXT_VALUE_2"""")
                            }
                            val result = expression.evaluate(envVars, context)

                            "then should be returned a value as Text type" {
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Text>()
                                value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                            }
                        }
                    }

                    "when the expression is mathematical" - {

                        "when the result is a number" - {
                            val expression = shouldBeSuccess {
                                parser.parse("$NUMBER_VALUE_1 + $NUMBER_VALUE_2")
                            }
                            val result = expression.evaluate(envVars, context)

                            "then should be returned a value as Decimal type" {
                                val value = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }

                        "when the result is a struct" - {
                            val expression = shouldBeSuccess {
                                parser.parse("""{ "$KEY_A": $NUMBER_VALUE_1 + $NUMBER_VALUE_2 }""")
                            }
                            val result = expression.evaluate(envVars, context)

                            "then should be returned a value as Struct type" {
                                val struct = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Struct>()
                                val value = struct[KEY_A].shouldBeInstanceOf<JsonElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }

                        "when the result is an array" - {
                            val expression = shouldBeSuccess {
                                parser.parse("""[$NUMBER_VALUE_1 + $NUMBER_VALUE_2]""")
                            }
                            val result = expression.evaluate(envVars, context)

                            "then should be returned a value as Array type" {
                                val array = result.shouldContainSuccessInstance()
                                    .shouldBeInstanceOf<JsonElement.Array>()
                                array.size shouldBe 1
                                val value = array[0].shouldBeInstanceOf<JsonElement.Decimal>()
                                value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                            }
                        }
                    }

                    "when the expression contains an array" - {
                        val expression = shouldBeSuccess {
                            parser.parse(
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
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Array type" {
                            val array = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Array>()
                            array shouldContainExactly listOf(
                                JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                JsonElement.Text(TEXT_VALUE_1),
                                JsonElement.Bool.valueOf(BOOL_VALUE_TRUE),
                                JsonElement.Bool.valueOf(BOOL_VALUE_FALSE),
                                JsonElement.Null,
                                JsonElement.Array(
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_3)),
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_4))
                                ),
                                JsonElement.Struct(
                                    KEY_A to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_5))
                                )
                            )
                        }
                    }

                    "when the expression contains a structure" - {
                        val expression = shouldBeSuccess {
                            parser.parse(
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
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Struct type" {
                            val struct = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Struct>()
                            struct shouldBe JsonElement.Struct(
                                KEY_A to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                KEY_B to JsonElement.Text(TEXT_VALUE_1),
                                KEY_C to JsonElement.Bool.valueOf(BOOL_VALUE_TRUE),
                                KEY_D to JsonElement.Bool.valueOf(BOOL_VALUE_FALSE),
                                KEY_E to JsonElement.Null,
                                KEY_F to JsonElement.Array(
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_3)),
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_4))
                                ),
                                KEY_G to JsonElement.Struct(
                                    KEY_H to JsonElement.Decimal(
                                        BigDecimal.valueOf(NUMBER_VALUE_5)
                                    )
                                )
                            )
                        }
                    }

                    "when the expression is mixed" - {
                        val expression = shouldBeSuccess {
                            parser.parse("[{a: 1}, {b: 2}].a")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Array type" {
                            val array = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Array>()
                            array shouldContainExactly listOf(
                                JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                JsonElement.Null,
                            )
                        }
                    }

                    "when the expression contains an unknown function" - {
                        val expression = shouldBeSuccess {
                            parser.parse("1 + abc()")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned the evaluation error" {
                            result.shouldContainFailureInstance()
                                .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                        }
                    }

                    "when the expression contains an assert" - {
                        val expression = shouldBeSuccess {
                            parser.parse("assert(a, a != null)")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned the evaluation error" {
                            result.shouldContainFailureInstance()
                                .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                        }
                    }
                }

                "when the expression contains variables from context" - {

                    "when the variable is a null value" - {
                        val envVars = envVarsMapOf()
                        val context = Context(sources = mapOf(Source(KEY_A) to JsonElement.Null))
                        val expression = shouldBeSuccess {
                            parser.parse(
                                """ if $KEY_A = null then $BOOL_VALUE_TRUE else $BOOL_VALUE_FALSE """
                            )
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Bool>()
                            value.get shouldBe BOOL_VALUE_TRUE
                        }
                    }

                    "when the variables are a boolean values" - {
                        val envVars = envVarsMapOf()
                        val context = Context(
                            sources = mapOf(
                                Source(KEY_A) to JsonElement.Bool.valueOf(BOOL_VALUE_TRUE),
                                Source(KEY_B) to JsonElement.Bool.valueOf(BOOL_VALUE_FALSE)
                            )
                        )
                        val expression = shouldBeSuccess {
                            parser.parse("$KEY_A and $KEY_B")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Bool>()
                            value.get shouldBe false
                        }
                    }

                    "when the variables are a text values" - {
                        val envVars = envVarsMapOf()
                        val context = Context(
                            sources = mapOf(
                                Source(KEY_A) to JsonElement.Text(TEXT_VALUE_1),
                                Source(KEY_B) to JsonElement.Text(TEXT_VALUE_2)
                            )
                        )
                        val expression = shouldBeSuccess {
                            parser.parse("""$KEY_A + " " + $KEY_B""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Text type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Text>()
                            value.get shouldBe "$TEXT_VALUE_1 $TEXT_VALUE_2"
                        }
                    }

                    "when the variables are a decimal values" - {
                        val envVars = envVarsMapOf()
                        val context = Context(
                            sources = mapOf(
                                Source(KEY_A) to JsonElement.Decimal(
                                    BigDecimal.valueOf(NUMBER_VALUE_1)
                                ),
                                Source(KEY_B) to JsonElement.Decimal(
                                    BigDecimal.valueOf(NUMBER_VALUE_2)
                                )
                            )
                        )
                        val expression = shouldBeSuccess {
                            parser.parse("$KEY_A + $KEY_B")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is an array value" - {
                        val envVars = envVarsMapOf()
                        val context = Context(
                            sources = mapOf(
                                Source(KEY_A) to JsonElement.Array(
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                    JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                                )
                            )
                        )
                        val expression = shouldBeSuccess {
                            parser.parse("""sum($KEY_A)""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is a struct value" - {
                        val envVars = envVarsMapOf()
                        val context = Context(
                            sources = mapOf(
                                Source(KEY_A) to JsonElement.Struct(
                                    KEY_B to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1))
                                ),
                                Source(KEY_C) to JsonElement.Struct(
                                    KEY_D to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                                )
                            )
                        )
                        val expression = shouldBeSuccess {
                            parser.parse("""$KEY_A.$KEY_B + $KEY_C.$KEY_D""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }
                }

                "when the expression contains variables from environment" - {

                    "when the variable is a null value" - {
                        val envVars = envVarsMapOf(ENV_VAR_1 to JsonElement.Null)
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse(
                                """ if $ENV_VAR_NAME_1 = null then $BOOL_VALUE_TRUE else $BOOL_VALUE_FALSE """
                            )
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Bool>()
                            value.get shouldBe BOOL_VALUE_TRUE
                        }
                    }

                    "when the variables are a boolean values" - {
                        val envVars = envVarsMapOf(
                            ENV_VAR_1 to JsonElement.Bool.valueOf(BOOL_VALUE_TRUE),
                            ENV_VAR_2 to JsonElement.Bool.valueOf(BOOL_VALUE_FALSE)
                        )
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse("$ENV_VAR_1 and $ENV_VAR_2")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Bool type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Bool>()
                            value.get shouldBe false
                        }
                    }

                    "when the variables are a text values" - {
                        val envVars = envVarsMapOf(
                            ENV_VAR_1 to JsonElement.Text(TEXT_VALUE_1),
                            ENV_VAR_2 to JsonElement.Text(TEXT_VALUE_2)
                        )
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse("""$ENV_VAR_1 + "-" + $ENV_VAR_2""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Text type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Text>()
                            value.get shouldBe "${TEXT_VALUE_1}-${TEXT_VALUE_2}"
                        }
                    }

                    "when the variables are a decimal values" - {
                        val envVars = envVarsMapOf(
                            ENV_VAR_1 to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                            ENV_VAR_2 to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                        )
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse("$ENV_VAR_1 + $ENV_VAR_2")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is an array value" - {
                        val envVars = envVarsMapOf(
                            ENV_VAR_1 to JsonElement.Array(
                                JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1)),
                                JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                            )
                        )
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse("""sum($ENV_VAR_1)""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }

                    "when the variable is a struct value" - {
                        val envVars = envVarsMapOf(
                            ENV_VAR_1 to JsonElement.Struct(
                                KEY_B to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_1))
                            ),
                            ENV_VAR_2 to JsonElement.Struct(
                                KEY_D to JsonElement.Decimal(BigDecimal.valueOf(NUMBER_VALUE_2))
                            )
                        )
                        val context = Context.empty()
                        val expression = shouldBeSuccess {
                            parser.parse("""$ENV_VAR_1.$KEY_B + $ENV_VAR_2.$KEY_D""")
                        }
                        val result = expression.evaluate(envVars, context)

                        "then should be returned a value as Decimal type" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Decimal>()
                            value.get shouldBe (NUMBER_VALUE_1 + NUMBER_VALUE_2).toBigDecimal()
                        }
                    }
                }
            }

            "when parsing an invalid expression" - {
                val result = parser.parse("1 ++ 1")

                "then should be returned the parsing error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<ExpressionParser.Errors.Parsing>()
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
        private const val ENV_VAR_NAME_1 = "env_var_1"
        private val ENV_VAR_1 = EnvVarName(ENV_VAR_NAME_1)
        private const val ENV_VAR_NAME_2 = "env_var_2"
        private val ENV_VAR_2 = EnvVarName(ENV_VAR_NAME_2)

        private val parser = feelExpressionParser(feelExpressionParserConfiguration(customFunctions = emptyList()))
    }
}
