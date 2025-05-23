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
import java.time.Month

@OptIn(AirfluxTypesExperimental::class)
internal class MonthFunctionTest : UnitTest() {

    init {

        "The `month` function" - {

            "when all parameters are specified and valid" - {
                (1..12).forEach { monthNumber ->
                    val month = Month.of(monthNumber)

                    "when month is $month" - {
                        val dateTime = LocalDateTime.of(YEAR, month, DAY, HOUR, MINUTE)
                            .format(DEFAULT_FORMATTER)
                        val expression =
                            shouldBeSuccess { parser.parse("""month("$dateTime", "$DEFAULT_DATA_TIME_FORMAT")""") }

                        val envVars = EnvVars.EMPTY
                        val context = Context.empty()
                        val result = expression.evaluate(envVars, context)

                        "then should be returned the number of the month" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Text>()
                            val actual = value.get
                            val expected = month.value.toString().padStart(2, '0')
                            actual shouldBe expected
                        }
                    }
                }
            }

            "when the value parameter is missing" - {
                val expression =
                    shouldBeSuccess { parser.parse("""month("$DEFAULT_DATA_TIME_FORMAT")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid date-time" - {
                val dateTime = "2023"
                val expression =
                    shouldBeSuccess { parser.parse("""month("$dateTime", "$DEFAULT_DATA_TIME_FORMAT")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid type" - {
                val expression =
                    shouldBeSuccess { parser.parse("""month(123, "$DEFAULT_DATA_TIME_FORMAT")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    val error = result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                    error.description.shouldContain(
                        "Invalid type of the parameter 'value'. Expected: ValString, but was: ValNumber"
                    )
                }
            }

            "when the value of the format parameter is not specified" - {
                (1..12).forEach { monthNumber ->
                    val month = Month.of(monthNumber)

                    "when month is $month" - {
                        val dateTime = LocalDateTime.of(YEAR, month, DAY, HOUR, MINUTE)
                            .format(DEFAULT_FORMATTER)
                        val expression =
                            shouldBeSuccess { parser.parse("""month("$dateTime", "")""") }

                        val envVars = EnvVars.EMPTY
                        val context = Context.empty()
                        val result = expression.evaluate(envVars, context)

                        "then should be returned the number of the month" {
                            val value = result.shouldContainSuccessInstance()
                                .shouldBeInstanceOf<JsonElement.Text>()
                            val actual = value.get
                            val expected = month.value.toString().padStart(2, '0')
                            actual shouldBe expected
                        }
                    }
                }
            }

            "when the format parameter is missing" - {
                val dateTime = DATE_TIME.format(DEFAULT_FORMATTER)
                val expression =
                    shouldBeSuccess { parser.parse("""month("$dateTime")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid date-time" - {
                val dateTime = "2023"
                val expression =
                    shouldBeSuccess { parser.parse("""month("$dateTime", "$DEFAULT_DATA_TIME_FORMAT")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid type" - {
                val dateTime = DATE_TIME.format(DEFAULT_FORMATTER)
                val expression =
                    shouldBeSuccess { parser.parse("""month("$dateTime", true)""") }

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
        private const val YEAR = 2025
        private const val DAY = 1
        private const val HOUR = 10
        private const val MINUTE = 23

        private val parser =
            feelExpressionParser(
                configuration = FeelExpressionParserConfiguration(
                    customFunctions = listOf(MonthFunction(DEFAULT_FORMATTER))
                )
            )
    }
}
