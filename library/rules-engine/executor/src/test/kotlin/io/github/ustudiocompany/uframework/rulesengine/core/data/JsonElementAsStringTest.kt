package io.github.ustudiocompany.uframework.rulesengine.core.data

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@OptIn(AirfluxTypesExperimental::class)
internal class JsonElementAsStringTest : UnitTest() {

    init {

        "The extension function `toStringValue` for the `JsonElement` type" - {

            listOf(
                JsonElement.Text(TEXT_VALUE) to TEXT_VALUE,
                JsonElement.Decimal(BigDecimal("10.5")) to "10.5",
                JsonElement.Bool.asTrue to "true",
                JsonElement.Bool.asFalse to "false",
                JsonElement.Null to "null",
                createArrayTestCase(),
                createStructTestCase()
            ).forEach { (jsonElement: JsonElement, expected: String) ->
                "should return the string representation of the value for the JSON element '$jsonElement'" {
                    val result = jsonElement.toStringValue()

                    result shouldBe expected
                }
            }
        }
    }

    private fun createArrayTestCase(): Pair<JsonElement, String> {
        val json = createArray()
        val expected = """["$TEXT_VALUE", $NUMBER_VALUE, $BOOL_TRUE_VALUE, $BOOL_FALSE_VALUE]"""
        return Pair(json, expected)
    }

    private fun createStructTestCase(): Pair<JsonElement, String> {
        val json = createStruct()

        val expected =
            """{"stringValue": "$TEXT_VALUE", "numberValue": $NUMBER_VALUE, "boolTrueValue": true, "boolFalseValue": false, "nullValue": null, "arrValue": ["$TEXT_VALUE", $NUMBER_VALUE, $BOOL_TRUE_VALUE, $BOOL_FALSE_VALUE]}"""
        return Pair(json, expected)
    }

    private fun createArray() =
        JsonElement.Array.Builder()
            .apply {
                add(JsonElement.Text(TEXT_VALUE))
                add(JsonElement.Decimal(BigDecimal(NUMBER_VALUE)))
                add(JsonElement.Bool.asTrue)
                add(JsonElement.Bool.asFalse)
            }
            .build()

    private fun createStruct() =
        JsonElement.Struct.Builder()
            .apply {
                set("stringValue", JsonElement.Text(TEXT_VALUE))
                set("numberValue", JsonElement.Decimal(BigDecimal(NUMBER_VALUE)))
                set("boolTrueValue", JsonElement.Bool.asTrue)
                set("boolFalseValue", JsonElement.Bool.asFalse)
                set("nullValue", JsonElement.Null)
                set("arrValue", createArray())
            }
            .build()

    private companion object {
        private const val TEXT_VALUE = "value-1"
        private const val NUMBER_VALUE = "10.5"
        private const val BOOL_TRUE_VALUE = "true"
        private const val BOOL_FALSE_VALUE = "false"
    }
}
