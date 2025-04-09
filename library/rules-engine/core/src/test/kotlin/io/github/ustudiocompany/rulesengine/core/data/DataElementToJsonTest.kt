package io.github.ustudiocompany.rulesengine.core.data

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class DataElementToJsonTest : UnitTest() {

    init {

        "The `toJson` function of the`DataElement` type" - {

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

        private fun testElement(): List<Pair<DataElement, String>> = listOf(
            DataElement.Null to "null",
            DataElement.Text("test") to "\"test\"",
            DataElement.Bool.valueOf(true) to "true",
            DataElement.Bool.valueOf(false) to "false",
            DataElement.Decimal(BigDecimal("1.0")) to "1.0",
            emptyArray(),
            arrayWithSimpleElements(),
            multiArray(),
            simpleStruct(),
            hierarchyStruct()
        )

        private fun emptyArray() = DataElement.Array(mutableListOf()) to "[]"

        private fun arrayWithSimpleElements() = DataElement.Array(
            mutableListOf(
                DataElement.Null,
                DataElement.Text("test"),
                DataElement.Bool.valueOf(true),
                DataElement.Bool.valueOf(false),
                DataElement.Decimal(BigDecimal("1.0"))
            )
        ) to "[null, \"test\", true, false, 1.0]"

        private fun multiArray() = DataElement.Array(
            mutableListOf(
                DataElement.Array(mutableListOf()),
                DataElement.Array(
                    mutableListOf(
                        DataElement.Decimal(BigDecimal("1")),
                        DataElement.Decimal(BigDecimal("2"))
                    )
                )
            )
        ) to "[[], [1, 2]]"

        private fun simpleStruct() = DataElement.Struct(
            mutableMapOf(
                "a" to DataElement.Null,
                "b" to DataElement.Text("test"),
                "c" to DataElement.Bool.valueOf(true),
                "d" to DataElement.Bool.valueOf(false),
                "e" to DataElement.Decimal(BigDecimal("1.0")),
                "f" to DataElement.Array(
                    mutableListOf(
                        DataElement.Decimal(BigDecimal("1")),
                        DataElement.Decimal(BigDecimal("2"))
                    )
                )
            )
        ) to """{"a": null, "b": "test", "c": true, "d": false, "e": 1.0, "f": [1, 2]}"""

        private fun hierarchyStruct() = DataElement.Struct(
            mutableMapOf(
                "a" to DataElement.Struct(
                    mutableMapOf(
                        "b" to DataElement.Text("test"),
                        "c" to DataElement.Bool.valueOf(true),
                        "d" to DataElement.Bool.valueOf(false),
                    )
                ),
                "e" to DataElement.Struct(
                    mutableMapOf(
                        "f" to DataElement.Decimal(BigDecimal("1.0"))
                    )
                )
            )
        ) to """{"a": {"b": "test", "c": true, "d": false}, "e": {"f": 1.0}}"""
    }
}
