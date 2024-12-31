package io.github.ustudiocompany.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operators.EQ
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class EqualOperatorTest : AbstractOperatorTest() {

    init {
        "The `equal` operator" - {
            withData(
                nameFn = ::testDescription,
                flatten(
                    compareNone(),
                    compareNullWithNull(),
                    compareNullWithOther(),
                    compareBooleanWithBoolean(),
                    compareBooleanWithOther(),
                    compareTextWithText(),
                    compareTextWithOther(),
                    compareNumberWithNumber(),
                    compareNumberWithOther(),
                    compareStructWithStruct(),
                    compareStructWithOther(),
                    compareArrayWithArray(),
                    compareArrayWithOther()
                )
            ) { (target, value, expected) ->
                val actual = EQ.compute(target = target, value = value)
                actual shouldBe expected
            }
        }
    }

    private fun compareNone() = listOf(
        TestData(target = null, value = null, expected = false),
        TestData(target = null, value = DataElement.Null, expected = false),
        TestData(target = null, value = bool(true), expected = false),
        TestData(target = null, value = bool(false), expected = false),
        TestData(target = null, value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = null, value = text(TEXT_VALUE_1), expected = false),
        TestData(target = null, value = struct(), expected = false),
        TestData(target = null, value = array(), expected = false)
    )

    private fun compareNullWithNull(): List<TestData> = listOf(
        TestData(target = DataElement.Null, value = DataElement.Null, expected = true)
    )

    private fun compareNullWithOther(): List<TestData> {
        val target = DataElement.Null
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false)
        )
    }

    private fun compareBooleanWithBoolean(): List<TestData> = listOf(
        TestData(target = bool(true), value = bool(true), expected = true),
        TestData(target = bool(false), value = bool(false), expected = true),
        TestData(target = bool(true), value = bool(false), expected = false),
        TestData(target = bool(false), value = bool(true), expected = false),
    )

    private fun compareBooleanWithOther(): List<TestData> = listOf(
        TestData(target = bool(true), value = null, expected = false),
        TestData(target = bool(false), value = null, expected = false),
        TestData(target = bool(true), value = DataElement.Null, expected = false),
        TestData(target = bool(false), value = DataElement.Null, expected = false),
        TestData(target = bool(true), value = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(false), value = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(true), value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(false), value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(true), value = struct(), expected = false),
        TestData(target = bool(false), value = struct(), expected = false),
        TestData(target = bool(true), value = array(), expected = false),
        TestData(target = bool(false), value = array(), expected = false)
    )

    private fun compareTextWithText(): List<TestData> = listOf(
        TestData(target = text(TEXT_VALUE_1), value = text(TEXT_VALUE_1), expected = true),
        TestData(target = text(TEXT_VALUE_EMPTY), value = text(TEXT_VALUE_1), expected = false),
        TestData(target = text(TEXT_VALUE_1), value = text(TEXT_VALUE_EMPTY), expected = false),
        TestData(target = text(TEXT_VALUE_1), value = text(TEXT_VALUE_2), expected = false)
    )

    private fun compareTextWithOther(): List<TestData> {
        val target = text(TEXT_VALUE_1)
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = DataElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false)
        )
    }

    private fun compareNumberWithNumber(): List<TestData> = listOf(
        TestData(target = decimal(NUMBER_VALUE_1), value = decimal(NUMBER_VALUE_1), expected = true),
        TestData(target = decimal(NUMBER_VALUE_1), value = decimal(NUMBER_VALUE_2), expected = false)
    )

    private fun compareNumberWithOther(): List<TestData> {
        val target = decimal(NUMBER_VALUE_1)
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = DataElement.Null, expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false)
        )
    }

    private fun compareStructWithStruct(): List<TestData> = listOf(
        TestData(target = struct(), value = struct(), expected = true),
        TestData(target = struct(KEY_1 to text(TEXT_VALUE_1)), value = struct(), expected = false),
        TestData(target = struct(), value = struct(KEY_1 to text(TEXT_VALUE_1)), expected = false),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            value = struct(KEY_1 to text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            value = struct(KEY_2 to text(TEXT_VALUE_1)),
            expected = false
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            value = struct(KEY_1 to text(TEXT_VALUE_2)),
            expected = false
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1), KEY_2 to text(TEXT_VALUE_2)),
            value = struct(KEY_1 to text(TEXT_VALUE_1)),
            expected = false
        )
    )

    private fun compareStructWithOther(): List<TestData> {
        val target = struct()
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = DataElement.Null, expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = array(), expected = false),
        )
    }

    private fun compareArrayWithArray(): List<TestData> = listOf(
        TestData(target = array(), value = array(), expected = true),
        TestData(target = array(text(TEXT_VALUE_1)), value = array(), expected = false),
        TestData(target = array(), value = array(text(TEXT_VALUE_1)), expected = false),
        TestData(target = array(text(TEXT_VALUE_1)), value = array(text(TEXT_VALUE_1)), expected = true),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            value = array(text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            value = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_2)),
            expected = false
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            value = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(target = array(text(TEXT_VALUE_1)), value = array(text(TEXT_VALUE_2)), expected = false),
        TestData(
            target = array(text(TEXT_VALUE_1), bool(true)),
            value = array(bool(true), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), bool(false)),
            value = array(text(TEXT_VALUE_1), bool(true)),
            expected = false
        )
    )

    private fun compareArrayWithOther(): List<TestData> {
        val target = array(text(TEXT_VALUE_1))
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = DataElement.Null, expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
        )
    }

    private companion object {
        private const val TEXT_VALUE_1 = "value"
        private const val TEXT_VALUE_2 = "VALUE"
        private const val TEXT_VALUE_EMPTY = ""
        private val NUMBER_VALUE_1 = BigDecimal(1)
        private val NUMBER_VALUE_2 = BigDecimal(2)

        private const val KEY_1 = "key-1"
        private const val KEY_2 = "key-2"
    }
}
