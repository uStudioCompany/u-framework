package io.github.ustudiocompany.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class EqualComparatorTest : AbstractComparatorTest() {

    init {
        "The equal comparator" - {
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
            ) { (target, compareWith, expected) ->
                val actual = Comparator.EQ.compare(target, compareWith)
                actual shouldBe expected
            }
        }
    }

    private fun compareNone() = listOf(
        TestData(target = null, compareWith = null, expected = false),
        TestData(target = null, compareWith = DataElement.Null, expected = false),
        TestData(target = null, compareWith = bool(true), expected = false),
        TestData(target = null, compareWith = bool(false), expected = false),
        TestData(target = null, compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = null, compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = null, compareWith = struct(), expected = false),
        TestData(target = null, compareWith = array(), expected = false)
    )

    private fun compareNullWithNull(): List<TestData> = listOf(
        TestData(target = DataElement.Null, compareWith = DataElement.Null, expected = true)
    )

    private fun compareNullWithOther(): List<TestData> {
        val target = DataElement.Null
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false)
        )
    }

    private fun compareBooleanWithBoolean(): List<TestData> = listOf(
        TestData(target = bool(true), compareWith = bool(true), expected = true),
        TestData(target = bool(false), compareWith = bool(false), expected = true),
        TestData(target = bool(true), compareWith = bool(false), expected = false),
        TestData(target = bool(false), compareWith = bool(true), expected = false),
    )

    private fun compareBooleanWithOther(): List<TestData> = listOf(
        TestData(target = bool(true), compareWith = null, expected = false),
        TestData(target = bool(false), compareWith = null, expected = false),
        TestData(target = bool(true), compareWith = DataElement.Null, expected = false),
        TestData(target = bool(false), compareWith = DataElement.Null, expected = false),
        TestData(target = bool(true), compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(false), compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(true), compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(false), compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(true), compareWith = struct(), expected = false),
        TestData(target = bool(false), compareWith = struct(), expected = false),
        TestData(target = bool(true), compareWith = array(), expected = false),
        TestData(target = bool(false), compareWith = array(), expected = false)
    )

    private fun compareTextWithText(): List<TestData> = listOf(
        TestData(target = text(TEXT_VALUE_1), compareWith = text(TEXT_VALUE_1), expected = true),
        TestData(target = text(TEXT_VALUE_EMPTY), compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = text(TEXT_VALUE_1), compareWith = text(TEXT_VALUE_EMPTY), expected = false),
        TestData(target = text(TEXT_VALUE_1), compareWith = text(TEXT_VALUE_2), expected = false)
    )

    private fun compareTextWithOther(): List<TestData> {
        val target = text(TEXT_VALUE_1)
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false)
        )
    }

    private fun compareNumberWithNumber(): List<TestData> = listOf(
        TestData(target = decimal(NUMBER_VALUE_1), compareWith = decimal(NUMBER_VALUE_1), expected = true),
        TestData(target = decimal(NUMBER_VALUE_1), compareWith = decimal(NUMBER_VALUE_2), expected = false)
    )

    private fun compareNumberWithOther(): List<TestData> {
        val target = decimal(NUMBER_VALUE_1)
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false)
        )
    }

    private fun compareStructWithStruct(): List<TestData> = listOf(
        TestData(target = struct(), compareWith = struct(), expected = true),
        TestData(target = struct(KEY_1 to text(TEXT_VALUE_1)), compareWith = struct(), expected = false),
        TestData(target = struct(), compareWith = struct(KEY_1 to text(TEXT_VALUE_1)), expected = false),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            compareWith = struct(KEY_1 to text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            compareWith = struct(KEY_2 to text(TEXT_VALUE_1)),
            expected = false
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1)),
            compareWith = struct(KEY_1 to text(TEXT_VALUE_2)),
            expected = false
        ),
        TestData(
            target = struct(KEY_1 to text(TEXT_VALUE_1), KEY_2 to text(TEXT_VALUE_2)),
            compareWith = struct(KEY_1 to text(TEXT_VALUE_1)),
            expected = false
        )
    )

    private fun compareStructWithOther(): List<TestData> {
        val target = struct()
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
        )
    }

    private fun compareArrayWithArray(): List<TestData> = listOf(
        TestData(target = array(), compareWith = array(), expected = true),
        TestData(target = array(text(TEXT_VALUE_1)), compareWith = array(), expected = false),
        TestData(target = array(), compareWith = array(text(TEXT_VALUE_1)), expected = false),
        TestData(target = array(text(TEXT_VALUE_1)), compareWith = array(text(TEXT_VALUE_1)), expected = true),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            compareWith = array(text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            compareWith = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_2)),
            expected = false
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
            compareWith = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(target = array(text(TEXT_VALUE_1)), compareWith = array(text(TEXT_VALUE_2)), expected = false),
        TestData(
            target = array(text(TEXT_VALUE_1), bool(true)),
            compareWith = array(bool(true), text(TEXT_VALUE_1)),
            expected = true
        ),
        TestData(
            target = array(text(TEXT_VALUE_1), bool(false)),
            compareWith = array(text(TEXT_VALUE_1), bool(true)),
            expected = false
        )
    )

    private fun compareArrayWithOther(): List<TestData> {
        val target = array(text(TEXT_VALUE_1))
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
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
