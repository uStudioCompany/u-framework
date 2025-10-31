package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsMapOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(AirfluxTypesExperimental::class)
internal class DateTimeGenerationFunctionTest : UnitTest() {

    init {

        "The `dateTime` function" - {

            "when the value of the format parameter is specified" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("$SPECIFIC_FORMAT")""") }
                val envVars = envVarsMapOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a value by format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = LocalDateTime.parse(value.get, SPECIFIC_FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the value of the format parameter is not specified" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("")""") }
                val envVars = envVarsMapOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a value by default format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = LocalDateTime.parse(value.get, DEFAULT_FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the format parameter is missing" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime()""") }
                val envVars = envVarsMapOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid date-time format" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("abc")""") }
                val envVars = envVarsMapOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid type" - {
                val expression =
                    shouldBeSuccess { parser.parse("""dateTime(true)""") }

                val envVars = envVarsMapOf()
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
        private const val SPECIFIC_FORMAT = "uuuu-MM-dd'T'HH'Z'"
        private val SPECIFIC_FORMATTER = DateTimeFormatter.ofPattern(SPECIFIC_FORMAT)

        private val parser =
            feelExpressionParser(
                configuration = FeelExpressionParserConfiguration(
                    customFunctions = listOf(
                        DateTimeGenerationFunction(DEFAULT_FORMATTER)
                    )
                )
            )
    }
}
