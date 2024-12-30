package io.github.ustudiocompany.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operator.Operators.IS_PRESENT
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class IsPresentOperatorTest : AbstractOperatorTest() {

    init {
        "The `is present` operator" - {
            withData(
                nameFn = ::testDescription,
                listOf(
                    TestData(target = null, compareWith = null, expected = false),
                    TestData(target = DataElement.Null, compareWith = null, expected = true),
                    TestData(target = bool(true), compareWith = null, expected = true),
                    TestData(target = bool(false), compareWith = null, expected = true),
                    TestData(target = text(TEXT_VALUE_1), compareWith = null, expected = true),
                    TestData(target = decimal(NUMBER_VALUE_1), compareWith = null, expected = true),
                    TestData(target = struct(), compareWith = null, expected = true),
                    TestData(target = array(), compareWith = null, expected = true)
                )
            ) { (target, compareWith, expected) ->
                val actual = IS_PRESENT.apply(target, compareWith)
                actual shouldBe expected
            }
        }
    }

    private companion object {
        private const val TEXT_VALUE_1 = "value"
        private val NUMBER_VALUE_1 = BigDecimal(1)
    }
}
