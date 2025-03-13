package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest

internal class ComputeLiteralValueTest : UnitTest() {

    init {
        "when value is literal type" - {
            val value = Value.Literal(DataElement.Text(VALUE))

            "then the compute function should return the fact-value" {
                val result = value.compute(CONTEXT)
                result shouldBeSuccess DataElement.Text(VALUE)
            }
        }
    }

    companion object {
        private val CONTEXT = Context.empty()
        private const val VALUE = "value"
    }
}
