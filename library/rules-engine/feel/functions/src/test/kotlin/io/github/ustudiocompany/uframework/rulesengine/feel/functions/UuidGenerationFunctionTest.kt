package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelExpressionParserConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.feelExpressionParser
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class UuidGenerationFunctionTest : UnitTest() {

    init {
        "The `uuid` function" - {
            val expression = shouldBeSuccess { parser.parse("uuid()") }

            "when a function is evaluated" - {
                val envVars = EnvVars.EMPTY
                val context = Context.empty()
                val result = expression.evaluate(envVars, context)

                "then should be returned a value by UUID format" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<JsonElement.Text>()
                    value.get shouldContain UUID_PATTERN
                }
            }
        }
    }

    private companion object {
        private val UUID_PATTERN = Regex(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )
        private val parser = feelExpressionParser(
            FeelExpressionParserConfiguration(
                listOf(UuidGenerationFunction())
            )
        )
    }
}
