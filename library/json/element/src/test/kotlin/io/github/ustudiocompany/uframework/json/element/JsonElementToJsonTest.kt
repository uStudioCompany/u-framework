package io.github.ustudiocompany.uframework.json.element

import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class JsonElementToJsonTest : UnitTest() {

    init {

        "The `toJson` function of the`JsonElement` type" - {

            testElement().forEach { (element, expected) ->

                "when the element is $element" - {

                    "then the function should return the corresponding value" {
                        element.toJson() shouldBe expected
                    }
                }
            }
        }
    }

    private companion object {

        private fun testElement(): List<Pair<JsonElement, String>> = listOf(
            JsonElement.Null to "null",
            JsonElement.Text("test") to "\"test\"",
            JsonElement.Bool.valueOf(true) to "true",
            JsonElement.Bool.valueOf(false) to "false",
            JsonElement.Decimal(BigDecimal("1.0")) to "1.0",
            emptyArray(),
            arrayWithSimpleElements(),
            multiArray(),
            simpleStruct(),
            hierarchyStruct()
        )

        private fun emptyArray() = JsonElement.Array() to "[]"

        private fun arrayWithSimpleElements() = JsonElement.Array(
            JsonElement.Null,
            JsonElement.Text("test"),
            JsonElement.Bool.valueOf(true),
            JsonElement.Bool.valueOf(false),
            JsonElement.Decimal(BigDecimal("1.0"))
        ) to "[null, \"test\", true, false, 1.0]"

        private fun multiArray() = JsonElement.Array(
            JsonElement.Array(),
            JsonElement.Array(
                JsonElement.Decimal(BigDecimal("1")),
                JsonElement.Decimal(BigDecimal("2"))
            )
        ) to "[[], [1, 2]]"

        private fun simpleStruct() = JsonElement.Struct(
            "a" to JsonElement.Null,
            "b" to JsonElement.Text("test"),
            "c" to JsonElement.Bool.valueOf(true),
            "d" to JsonElement.Bool.valueOf(false),
            "e" to JsonElement.Decimal(BigDecimal("1.0")),
            "f" to JsonElement.Array(
                JsonElement.Decimal(BigDecimal("1")),
                JsonElement.Decimal(BigDecimal("2"))
            )
        ) to """{"a": null, "b": "test", "c": true, "d": false, "e": 1.0, "f": [1, 2]}"""

        private fun hierarchyStruct() = JsonElement.Struct(
            "a" to JsonElement.Struct(
                "b" to JsonElement.Text("test"),
                "c" to JsonElement.Bool.valueOf(true),
                "d" to JsonElement.Bool.valueOf(false),
            ),
            "e" to JsonElement.Struct(
                "f" to JsonElement.Decimal(BigDecimal("1.0"))
            )
        ) to """{"a": {"b": "test", "c": true, "d": false}, "e": {"f": 1.0}}"""
    }
}
