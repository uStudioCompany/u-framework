package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.Deserializer
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class ValueModelDeserializationTest : UnitTest() {

    init {

        "The ValueModel type" - {

            "when JSON has a literal value" - {
                val json = """
                    | {
                    |   "kind": "fact",
                    |   "fact": "$FACT"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return a literal value" {
                    result shouldBe ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                }
            }

            "when JSON has a reference value" - {
                val json = """
                    | {
                    |   "kind": "reference",
                    |   "source": "$SOURCE",
                    |   "path": "$PATH"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return a reference value" {
                    result shouldBe ValueModel.Reference(source = SOURCE, path = PATH)
                }
            }

            "when JSON has an expression value" - {
                val json = """
                    | {
                    |   "kind": "expression",
                    |   "expression": "$EXPRESSION"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ValueModel::class.java)

                "then should be return an expression value" {
                    result shouldBe ValueModel.Expression(expression = EXPRESSION)
                }
            }
        }
    }

    private companion object {
        private const val SOURCE = "input_body"
        private const val PATH = "$.scheme"
        private const val FACT = "UA"
        private const val EXPRESSION = "1 + 2"

        private val deserializer = Deserializer()
    }
}
