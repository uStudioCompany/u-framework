package io.github.ustudiocompany.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operator.Operators.IN
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@Suppress("CyclomaticComplexMethod")
internal class InOperatorTest : AbstractOperatorTest() {

    init {
        "The `in` operator" - {
            withData(
                nameFn = ::testDescription,
                flatten(
                    compareNone(),
                    compareNull(),
                    compareBoolean(),
                    compareText(),
                    compareNumber(),
                    compareStruct(),
                    compareArray()
                )
            ) { (target, compareWith, expected) ->
                val actual = IN.apply(target, compareWith)
                actual shouldBe expected
            }
        }
    }

    private fun compareNone() = listOf(
        TestData(target = null, compareWith = null, expected = false),
        TestData(target = null, compareWith = DataElement.Null, expected = false),
        TestData(target = null, compareWith = bool(true), expected = false),
        TestData(target = null, compareWith = bool(false), expected = false),
        TestData(target = null, compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = null, compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = null, compareWith = struct(KEY_1 to text(TEXT_VALUE_1)), expected = false),
        TestData(target = null, compareWith = array(text(TEXT_VALUE_1)), expected = false)
    )

    private fun compareNull(): List<TestData> {
        val target = DataElement.Null
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
            TestData(target = target, compareWith = array(DataElement.Null), expected = true)
        )
    }

    private fun compareBoolean(): List<TestData> = listOf(
        TestData(target = bool(true), compareWith = null, expected = false),
        TestData(target = bool(false), compareWith = null, expected = false),
        TestData(target = bool(true), compareWith = bool(true), expected = false),
        TestData(target = bool(false), compareWith = bool(false), expected = false),
        TestData(target = bool(true), compareWith = bool(false), expected = false),
        TestData(target = bool(false), compareWith = bool(true), expected = false),
        TestData(target = bool(true), compareWith = DataElement.Null, expected = false),
        TestData(target = bool(false), compareWith = DataElement.Null, expected = false),
        TestData(target = bool(true), compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(false), compareWith = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(true), compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(false), compareWith = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(true), compareWith = struct(), expected = false),
        TestData(target = bool(false), compareWith = struct(), expected = false),
        TestData(target = bool(true), compareWith = array(), expected = false),
        TestData(target = bool(false), compareWith = array(), expected = false),
        TestData(target = bool(true), compareWith = array(bool(true)), expected = true),
        TestData(target = bool(true), compareWith = array(bool(false)), expected = false),
        TestData(target = bool(true), compareWith = array(text(TEXT_VALUE_1), bool(true)), expected = true),
        TestData(target = bool(false), compareWith = array(bool(false)), expected = true),
        TestData(target = bool(false), compareWith = array(bool(true)), expected = false),
        TestData(target = bool(false), compareWith = array(text(TEXT_VALUE_1), bool(false)), expected = true)
    )

    private fun compareText(): List<TestData> {
        val target = text(TEXT_VALUE_1)
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
            TestData(target = target, compareWith = array(text(TEXT_VALUE_1)), expected = true)
        )
    }

    private fun compareNumber(): List<TestData> {
        val target = decimal(NUMBER_VALUE_1)
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
            TestData(target = target, compareWith = array(decimal(NUMBER_VALUE_1)), expected = true),
            TestData(
                target = target,
                compareWith = array(text(TEXT_VALUE_1), decimal(NUMBER_VALUE_1)),
                expected = true
            ),
            TestData(target = target, compareWith = array(text(TEXT_VALUE_1)), expected = false),
        )
    }

    private fun compareStruct(): List<TestData> {
        val target = struct()
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
            TestData(target = target, compareWith = array(struct()), expected = true),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                compareWith = array(
                    struct(KEY_1 to text(TEXT_VALUE_1))
                ),
                expected = true
            ),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                compareWith = array(
                    struct(KEY_2 to text(TEXT_VALUE_1))
                ),
                expected = false
            ),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                compareWith = array(
                    struct(KEY_1 to text(TEXT_VALUE_2))
                ),
                expected = false
            ),
        )
    }

    private fun compareArray(): List<TestData> {
        val target = array(text(TEXT_VALUE_1))
        return listOf(
            TestData(target = target, compareWith = null, expected = false),
            TestData(target = target, compareWith = DataElement.Null, expected = false),
            TestData(target = target, compareWith = bool(true), expected = false),
            TestData(target = target, compareWith = bool(false), expected = false),
            TestData(target = target, compareWith = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, compareWith = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, compareWith = struct(), expected = false),
            TestData(target = target, compareWith = array(), expected = false),
            TestData(target = array(text(TEXT_VALUE_1)), compareWith = array(text(TEXT_VALUE_1)), expected = true),
            TestData(
                target = array(text(TEXT_VALUE_1)),
                compareWith = array(bool(true), text(TEXT_VALUE_1)),
                expected = true
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
                compareWith = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
                expected = false
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
                compareWith = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
                expected = true
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1)),
                compareWith = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
                expected = true
            ),
        )
    }

    private companion object {
        private const val TEXT_VALUE_1 = "value"
        private const val TEXT_VALUE_2 = "VALUE"
        private val NUMBER_VALUE_1 = BigDecimal(1)

        private const val KEY_1 = "key-1"
        private const val KEY_2 = "key-2"
    }
}
