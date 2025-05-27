package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.test.kotest.UnitTest

@OptIn(AirfluxTypesExperimental::class)
internal class ComputeLiteralValueTest : UnitTest() {

    init {
        "when the value is the Literal type" - {
            val value = Value.Literal(JsonElement.Text(VALUE))

            "then the compute function should return the fact-value" {
                val result = value.compute(ENV_VARS, CONTEXT)
                result shouldBeSuccess JsonElement.Text(VALUE)
            }
        }
    }

    companion object {
        private val ENV_VARS = EnvVars.EMPTY
        private val CONTEXT = Context.empty()
        private const val VALUE = "value"
    }
}
