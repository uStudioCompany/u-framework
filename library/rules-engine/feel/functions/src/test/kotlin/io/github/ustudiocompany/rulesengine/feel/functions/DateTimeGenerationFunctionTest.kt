package io.github.ustudiocompany.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.DateTimeGenerationFunction
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(AirfluxTypesExperimental::class)
internal class DateTimeGenerationFunctionTest : UnitTest() {

    private val engine = FeelEngine(FeelEngineConfiguration(listOf(DateTimeGenerationFunction())))

    init {
        "The `dateTime` function" - {

            "when the engine evaluates the expression the function with valid format" - {
                val expression = shouldBeSuccess { engine.parse("""dateTime("$SPECIFIC_FORMAT")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then the engine should return an value by format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<DataElement.Text>()
                    val actual = LocalDateTime.parse(value.get, FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the engine evaluates the expression with the function without format" - {
                val expression = shouldBeSuccess { engine.parse("""dateTime("")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then the engine should return an value by default format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<DataElement.Text>()
                    val actual = LocalDateTime.parse(value.get, DEFAULT_FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the engine evaluates the expression with the function without parameter" - {
                val expression = shouldBeSuccess { engine.parse("""dateTime()""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then the engine should return the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the engine evaluates the expression the function with invalid format" - {
                val expression = shouldBeSuccess { engine.parse("""dateTime("abc")""") }
                val context = Context.empty()
                val result = expression.evaluate(context)

                "then the engine should return the evaluation error" {
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
    }
}
