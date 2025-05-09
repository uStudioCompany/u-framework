package io.github.ustudiocompany.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement
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

    protected fun text(value: String) = JsonElement.Text(value)
    protected fun decimal(value: Number) = JsonElement.Decimal(BigDecimal(value.toString()))
    protected fun bool(value: Boolean) = JsonElement.Bool.valueOf(value)
    protected fun struct(vararg properties: Pair<String, JsonElement>) =
        JsonElement.Struct.Builder()
            .apply { properties.forEach { (key, value) -> this[key] = value } }
            .build()

    protected fun array(vararg items: JsonElement) = JsonElement.Array.Builder()
        .apply { items.forEach { this.add(it) } }
        .build()

    protected data class TestData(
        val target: JsonElement?,
        val value: JsonElement?,
        val expected: Boolean
    )
}
