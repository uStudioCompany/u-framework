package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime

@OptIn(AirfluxTypesExperimental::class)
internal class YearFunctionTest : UnitTest() {

    init {

        "The `year` function" - {

            "when all parameters are specified and valid" - {
                val value = VALID_VALUE
                val format = VALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("""year($value, $format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a value by format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = value.get
                    actual shouldBe EXPECTED
                }
            }

            "when the value parameter is missing" - {
                val format = VALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("""year($format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid date-time" - {
                val value = INVALID_VALUE
                val format = VALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("""year($value, $format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid type" - {
                val value = INVALID_VALUE_TYPE
                val format = VALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("""year($value, $format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    val error = result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                    error.description.shouldContain(
                        "Invalid type of the parameter 'value'. Expected: ValString, but was: ValBoolean"
                    )
                }
            }

            "when the value of the format parameter is not specified" - {
                val value = VALID_VALUE
                val expression = shouldBeSuccess { parser.parse("""year($value, "")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a value by default format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = value.get
                    actual shouldBe EXPECTED
                }
            }

            "when the format parameter is missing" - {
                val value = VALID_VALUE
                val expression = shouldBeSuccess { parser.parse("""year($value)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid date-time" - {
                val value = VALID_VALUE
                val format = INVALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("""year($value, $format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid type" - {
                val value = VALID_VALUE
                val format = INVALID_FORMAT_TYPE
                val expression = shouldBeSuccess { parser.parse("""year($value, $format)""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    val error = result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()

                    error.description.shouldContain(
                        "Invalid type of the parameter 'format'. Expected: ValString, but was: ValBoolean"
                    )
                }
            }
        }
    }

    private companion object {
        private val DATE_TIME = LocalDateTime.now()
        private val VALID_VALUE = """ "${DATE_TIME.format(DEFAULT_FORMATTER)}" """
        private const val INVALID_VALUE = "\"2025\""
        private const val INVALID_VALUE_TYPE = "true"
        private const val VALID_FORMAT = """ "$DEFAULT_DATA_TIME_FORMAT" """
        private const val INVALID_FORMAT = "\"abc\""
        private const val INVALID_FORMAT_TYPE = "true"
        private val EXPECTED = DATE_TIME.year.toString()

        private val parser =
            feelExpressionParser(
                configuration = FeelExpressionParserConfiguration(
                    customFunctions = listOf(YearFunction(DEFAULT_FORMATTER))
                )
            )
    }
}
