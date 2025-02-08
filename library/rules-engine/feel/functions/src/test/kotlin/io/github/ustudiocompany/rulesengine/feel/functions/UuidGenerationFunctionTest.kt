package io.github.ustudiocompany.rulesengine.feel.functions

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngine
import io.github.ustudiocompany.uframework.rulesengine.feel.FeelEngineConfiguration
import io.github.ustudiocompany.uframework.rulesengine.feel.functions.UuidGenerationFunction
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class UuidGenerationFunctionTest : UnitTest() {

    private val engine = FeelEngine(FeelEngineConfiguration(listOf(UuidGenerationFunction())))

    init {
        "The `uuid` function" - {
            val expression = shouldBeSuccess { engine.parse("uuid()") }

            "when the engine evaluates the expression" - {
                val variables = emptyMap<Source, DataElement>()
                val result = expression.evaluate(variables)

                "then the engine should return an value by UUID format" {
                    result.shouldBeSuccess()
                    val value = result.value.shouldBeInstanceOf<DataElement.Text>()
                    value.get shouldContain UUID_PATTERN
                }
            }
        }
    }

    private companion object {
        private val UUID_PATTERN = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    }
}
