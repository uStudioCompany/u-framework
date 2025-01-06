package io.github.ustudiocompany.rulesengine.feel.function

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.types.shouldBeInstanceOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class DateTimeGenerationFunctionTest : UnitTest() {

    private val engine = FeelEngine(FeelEngineConfiguration())

    init {
        "The `dateTime` function" - {
            val expression = """dateTime("$FORMAT")"""

            "when the engine evaluates the expression" - {
                val variables = emptyMap<String, DataElement>()
                val result = engine.parse(expression)
                    .andThen { expression -> engine.evaluate(expression, variables) }

                "then the engine should return an value by DataTime format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                    val actual = LocalDateTime.parse(value.get, FORMATTER)
                    val expected = LocalDateTime.now()
                    actual shouldBeLessThan expected
                }
            }
        }
    }

    private companion object {
        private const val FORMAT = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val FORMATTER = DateTimeFormatter.ofPattern(FORMAT)
    }
}
