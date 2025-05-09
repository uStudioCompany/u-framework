package io.github.ustudiocompany.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.BooleanOperators.IN
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
            ) { (target, value, expected) ->
                val actual = IN.compute(target = target, value = value)
                actual shouldBe expected
            }
        }
    }

    private fun compareNone() = listOf(
        TestData(target = null, value = null, expected = false),
        TestData(target = null, value = JsonElement.Null, expected = false),
        TestData(target = null, value = bool(true), expected = false),
        TestData(target = null, value = bool(false), expected = false),
        TestData(target = null, value = text(TEXT_VALUE_1), expected = false),
        TestData(target = null, value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = null, value = struct(KEY_1 to text(TEXT_VALUE_1)), expected = false),
        TestData(target = null, value = array(text(TEXT_VALUE_1)), expected = false)
    )

    private fun compareNull(): List<TestData> {
        val target = JsonElement.Null
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = JsonElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false),
            TestData(target = target, value = array(JsonElement.Null), expected = true)
        )
    }

    private fun compareBoolean(): List<TestData> = listOf(
        TestData(target = bool(true), value = null, expected = false),
        TestData(target = bool(false), value = null, expected = false),
        TestData(target = bool(true), value = bool(true), expected = false),
        TestData(target = bool(false), value = bool(false), expected = false),
        TestData(target = bool(true), value = bool(false), expected = false),
        TestData(target = bool(false), value = bool(true), expected = false),
        TestData(target = bool(true), value = JsonElement.Null, expected = false),
        TestData(target = bool(false), value = JsonElement.Null, expected = false),
        TestData(target = bool(true), value = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(false), value = text(TEXT_VALUE_1), expected = false),
        TestData(target = bool(true), value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(false), value = decimal(NUMBER_VALUE_1), expected = false),
        TestData(target = bool(true), value = struct(), expected = false),
        TestData(target = bool(false), value = struct(), expected = false),
        TestData(target = bool(true), value = array(), expected = false),
        TestData(target = bool(false), value = array(), expected = false),
        TestData(target = bool(true), value = array(bool(true)), expected = true),
        TestData(target = bool(true), value = array(bool(false)), expected = false),
        TestData(target = bool(true), value = array(text(TEXT_VALUE_1), bool(true)), expected = true),
        TestData(target = bool(false), value = array(bool(false)), expected = true),
        TestData(target = bool(false), value = array(bool(true)), expected = false),
        TestData(target = bool(false), value = array(text(TEXT_VALUE_1), bool(false)), expected = true)
    )

    private fun compareText(): List<TestData> {
        val target = text(TEXT_VALUE_1)
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = JsonElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false),
            TestData(target = target, value = array(text(TEXT_VALUE_1)), expected = true)
        )
    }

    private fun compareNumber(): List<TestData> {
        val target = decimal(NUMBER_VALUE_1)
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = JsonElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false),
            TestData(target = target, value = array(decimal(NUMBER_VALUE_1)), expected = true),
            TestData(
                target = target,
                value = array(text(TEXT_VALUE_1), decimal(NUMBER_VALUE_1)),
                expected = true
            ),
            TestData(target = target, value = array(text(TEXT_VALUE_1)), expected = false),
        )
    }

    private fun compareStruct(): List<TestData> {
        val target = struct()
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = JsonElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false),
            TestData(target = target, value = array(struct()), expected = true),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                value = array(
                    struct(KEY_1 to text(TEXT_VALUE_1))
                ),
                expected = true
            ),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                value = array(
                    struct(KEY_2 to text(TEXT_VALUE_1))
                ),
                expected = false
            ),
            TestData(
                target = struct(KEY_1 to text(TEXT_VALUE_1)),
                value = array(
                    struct(KEY_1 to text(TEXT_VALUE_2))
                ),
                expected = false
            ),
        )
    }

    private fun compareArray(): List<TestData> {
        val target = array(text(TEXT_VALUE_1))
        return listOf(
            TestData(target = target, value = null, expected = false),
            TestData(target = target, value = JsonElement.Null, expected = false),
            TestData(target = target, value = bool(true), expected = false),
            TestData(target = target, value = bool(false), expected = false),
            TestData(target = target, value = text(TEXT_VALUE_1), expected = false),
            TestData(target = target, value = decimal(NUMBER_VALUE_1), expected = false),
            TestData(target = target, value = struct(), expected = false),
            TestData(target = target, value = array(), expected = false),
            TestData(target = array(text(TEXT_VALUE_1)), value = array(text(TEXT_VALUE_1)), expected = true),
            TestData(
                target = array(text(TEXT_VALUE_1)),
                value = array(bool(true), text(TEXT_VALUE_1)),
                expected = true
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
                value = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
                expected = false
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
                value = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1), text(TEXT_VALUE_2)),
                expected = true
            ),
            TestData(
                target = array(text(TEXT_VALUE_1), text(TEXT_VALUE_1)),
                value = array(text(TEXT_VALUE_1), text(TEXT_VALUE_2), text(TEXT_VALUE_1)),
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
