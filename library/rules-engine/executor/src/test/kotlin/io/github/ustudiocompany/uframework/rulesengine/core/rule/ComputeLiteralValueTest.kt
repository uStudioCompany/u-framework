package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.test.kotest.UnitTest

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeLiteralValueTest : UnitTest() {

    init {
        "when value is literal type" - {
            val value = Value.Literal(JsonElement.Text(VALUE))

            "then the compute function should return the fact-value" {
                val result = value.compute(CONTEXT)
                result shouldBeSuccess JsonElement.Text(VALUE)
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()
        private const val VALUE = "value"
    }
}
