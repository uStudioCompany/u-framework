package io.github.ustudiocompany.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import java.math.BigDecimal

internal abstract class AbstractOperatorTest : UnitTest() {
    protected fun flatten(vararg lists: List<TestData>) = lists.flatMap { it }
    protected fun testDescription(data: TestData): String {
        val targetValue = if (data.target == null)
            "none"
        else
            "${data.target::class.simpleName}(${data.target})"
        val compareWithValue = if (data.value == null)
            "none"
        else
            "${data.value::class.simpleName}(${data.value})"
        val expectedValue = if (data.expected) "should be equal" else "should not be equal"
        return "$targetValue $expectedValue $compareWithValue"
    }

    protected fun text(value: String) = DataElement.Text(value)
    protected fun decimal(value: Number) = DataElement.Decimal(BigDecimal(value.toString()))
    protected fun bool(value: Boolean) = DataElement.Bool(value)
    protected fun struct(vararg properties: Pair<String, DataElement>) =
        DataElement.Struct(properties.toMap().toMutableMap())

    protected fun array(vararg items: DataElement) = DataElement.Array(items.toMutableList())

    protected data class TestData(
        val target: DataElement?,
        val value: DataElement?,
        val expected: Boolean
    )
}
