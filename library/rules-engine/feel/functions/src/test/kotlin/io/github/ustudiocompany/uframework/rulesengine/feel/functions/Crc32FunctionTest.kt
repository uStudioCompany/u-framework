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
import java.util.zip.CRC32

@OptIn(AirfluxTypesExperimental::class)
internal class Crc32FunctionTest : UnitTest() {

    init {

        "The `crc32` function" - {

            "when the value parameter is specified" - {
                val expression = shouldBeSuccess { parser.parse("""crc32("$TEXT_VALUE")""") }

                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a hash by input value" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    val crc32 = CRC32()
                    crc32.update(TEXT_VALUE.toByteArray())
                    val expected = crc32.value.toString()
                    value.get shouldBe expected
                }
            }

            "when the value of the value parameter is not a valid type" - {
                val expression = shouldBeSuccess { parser.parse("crc32(true)") }

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
        }
    }

    private companion object {
        private const val TEXT_VALUE = "Hello"
        private val parser = feelExpressionParser(
            FeelExpressionParserConfiguration(
                listOf(Crc32Function())
            )
        )
    }
}
