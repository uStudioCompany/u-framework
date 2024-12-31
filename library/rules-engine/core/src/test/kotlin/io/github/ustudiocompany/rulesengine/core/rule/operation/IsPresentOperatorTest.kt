package io.github.ustudiocompany.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operators.IS_PRESENT
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class IsPresentOperatorTest : AbstractOperatorTest() {

    init {
        "The `is present` operator" - {
            withData(
                nameFn = ::testDescription,
                listOf(
                    TestData(target = null, value = null, expected = false),
                    TestData(target = DataElement.Null, value = null, expected = true),
                    TestData(target = bool(true), value = null, expected = true),
                    TestData(target = bool(false), value = null, expected = true),
                    TestData(target = text(TEXT_VALUE_1), value = null, expected = true),
                    TestData(target = decimal(NUMBER_VALUE_1), value = null, expected = true),
                    TestData(target = struct(), value = null, expected = true),
                    TestData(target = array(), value = null, expected = true)
                )
            ) { (target, value, expected) ->
                val actual = IS_PRESENT.compute(target = target, value = value)
                actual shouldBe expected
            }
        }
    }

    private companion object {
        private const val TEXT_VALUE_1 = "value"
        private val NUMBER_VALUE_1 = BigDecimal(1)
    }
}
