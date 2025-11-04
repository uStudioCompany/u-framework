package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainFailureInstance
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.envVarsOf
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class Crc32FunctionTest : UnitTest() {

    init {

        "The `crc32` function" - {

            "when all parameters are specified and valid" - {
                val value = TEXT_VALUE
                val format = HEX_FORMAT
                val expression = shouldBeSuccess { parser.parse("crc32($value, $format)") }

                val envVars = envVarsOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a hash by input value" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    value.get shouldBe EXPECTED
                }
            }

            "when the value parameter is missing" - {
                val format = HEX_FORMAT
                val expression = shouldBeSuccess { parser.parse("crc32($format)") }

                val envVars = envVarsOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the value parameter is not a valid type" - {
                val value = BOOL_VALUE
                val format = HEX_FORMAT
                val expression = shouldBeSuccess { parser.parse("crc32($value, $format)") }

                val envVars = envVarsOf()
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

            "when the format parameter is missing" - {
                val value = TEXT_VALUE
                val expression = shouldBeSuccess { parser.parse("crc32($value)") }

                val envVars = envVarsOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid" - {
                val value = TEXT_VALUE
                val format = INVALID_FORMAT
                val expression = shouldBeSuccess { parser.parse("crc32($value, $format)") }

                val envVars = envVarsOf()
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned the evaluation error" {
                    result.shouldContainFailureInstance()
                        .shouldBeInstanceOf<FeelExpression.EvaluateError>()
                }
            }

            "when the value of the format parameter is not a valid type" - {
                val value = TEXT_VALUE
                val format = BOOL_VALUE
                val expression = shouldBeSuccess { parser.parse("crc32($value, $format)") }

                val envVars = envVarsOf()
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
        private const val TEXT_VALUE = "\"Hello\""
        private const val BOOL_VALUE = "true"
        private const val HEX_FORMAT = "\"%08X\""
        private const val INVALID_FORMAT = "\"%abc\""
        private const val EXPECTED = "F7D18982"

        private val parser = feelExpressionParser(
            FeelExpressionParserConfiguration(
                listOf(Crc32Function())
            )
        )
    }
}
