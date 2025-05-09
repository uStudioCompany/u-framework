package io.github.ustudiocompany.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.DateTimeGenerationFunction
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(AirfluxTypesExperimental::class)
internal class DateTimeGenerationFunctionTest : UnitTest() {

    init {
        "The `dateTime` function" - {

            "when a function with a valid format is evaluated" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("$SPECIFIC_FORMAT")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then should be returned a value by format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = LocalDateTime.parse(value.get, FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when a function without format is evaluated" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then should be returned a value by default format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val actual = LocalDateTime.parse(value.get, DEFAULT_FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when a function without parameter is evaluated" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime()""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when a function with invalid format is evaluated" - {
                val expression = shouldBeSuccess { parser.parse("""dateTime("abc")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }
        }
    }

    private companion object {
        private const val SPECIFIC_FORMAT = "uuuu-MM-dd'T'HH'Z'"
        private val FORMATTER = DateTimeFormatter.ofPattern(SPECIFIC_FORMAT)
        private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DateTimeGenerationFunction.Companion.DEFAULT_FORMAT)

        private val parser =
            feelExpressionParser(FeelExpressionParserConfiguration(listOf(DateTimeGenerationFunction())))
    }
}
