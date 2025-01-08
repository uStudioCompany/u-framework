package io.github.ustudiocompany.rulesengine.feel.functions

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.DateTimeGenerationFunction
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class DateTimeGenerationFunctionTest : UnitTest() {

    private val engine = FeelEngine(FeelEngineConfiguration(listOf(DateTimeGenerationFunction())))

    init {
        "The `dateTime` function" - {

            "when the engine evaluates the expression the function with valid format" - {
                val expression = """dateTime("$SPECIFIC_FORMAT")"""
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                    val actual = LocalDateTime.parse(value.get, FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the engine evaluates the expression with the function without format" - {
                val expression = """dateTime("")"""
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by default format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                    val actual = LocalDateTime.parse(value.get, DEFAULT_FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }

            "when the engine evaluates the expression with the function without parameter" - {
                val expression = """dateTime()"""
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return the evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
                }
            }

            "when the engine evaluates the expression the function with invalid format" - {
                val expression = """dateTime("abc")"""
                val variables = emptyMap<Source, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return the evaluation error" {
                    result.shouldBeFailure()
                    result.cause.shouldBeInstanceOf<FeelEngine.Errors.Evaluate>()
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
