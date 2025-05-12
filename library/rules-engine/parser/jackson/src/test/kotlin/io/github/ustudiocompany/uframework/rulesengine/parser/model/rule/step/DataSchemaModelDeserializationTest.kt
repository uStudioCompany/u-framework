package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.JacksonDeserializer
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataSchemaModelDeserializationTest : UnitTest() {

    init {

        "The DataSchemaModel type" - {

            "when a schema specifies a struct with simple properties as an element" - {
                val json = """
                    | {
                    |   "type": "struct",
                    |   "properties": [
                    |     {
                    |       "type": "value",
                    |       "name": "$FIRST_PROP_NAME",
                    |       "value": {
                    |         "kind": "reference",
                    |         "source": "$SOURCE",
                    |         "path": "$PATH"
                    |       }
                    |     },
                    |     {
                    |       "type": "value",
                    |       "name": "$SECOND_PROP_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$FACT"
                    |       }
                    |     },
                    |     {
                    |       "type": "value",
                    |       "name": "$THIRD_PROP_NAME",
                    |       "value": {
                    |         "kind": "expression",
                    |         "expression": "$EXPRESSION"
                    |       }
                    |     }
                    |   ]
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, DataSchemaModel::class.java)

                "then should be return a struct" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<DataSchemaModel>()

                    value shouldBe DataSchemaModel.Struct(
                        properties = listOf(
                            DataSchemaModel.StructProperty.Element(
                                name = FIRST_PROP_NAME,
                                value = ValueModel.Reference(source = SOURCE, path = PATH)
                            ),
                            DataSchemaModel.StructProperty.Element(
                                name = SECOND_PROP_NAME,
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            ),
                            DataSchemaModel.StructProperty.Element(
                                name = THIRD_PROP_NAME,
                                value = ValueModel.Expression(expression = EXPRESSION)
                            )
                        )
                    )
                }
            }

            "when a schema specifies a array with a simple elements" - {
                val json = """
                    | {
                    |   "type": "array",
                    |   "items": [
                    |     {
                    |       "type": "value",
                    |       "value": {
                    |         "kind": "reference",
                    |         "source": "$SOURCE",
                    |         "path": "$PATH"
                    |       }
                    |     },
                    |     {
                    |       "type": "value",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$FACT"
                    |       }
                    |     },
                    |     {
                    |       "type": "value",
                    |       "value": {
                    |         "kind": "expression",
                    |         "expression": "$EXPRESSION"
                    |       }
                    |     }
                    |   ]
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, DataSchemaModel::class.java)

                "then should be return an array" {
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<DataSchemaModel>()

                    value shouldBe DataSchemaModel.Array(
                        items = listOf(
                            DataSchemaModel.ArrayItem.Element(
                                value = ValueModel.Reference(source = SOURCE, path = PATH)
                            ),
                            DataSchemaModel.ArrayItem.Element(
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            ),
                            DataSchemaModel.ArrayItem.Element(
                                value = ValueModel.Expression(expression = EXPRESSION)
                            )
                        )
                    )
                }
            }
        }
    }

    private companion object {
        private const val FIRST_PROP_NAME = "first"
        private const val SECOND_PROP_NAME = "second"
        private const val THIRD_PROP_NAME = "third"
        private const val SOURCE = "input_body"
        private const val PATH = "$.scheme"
        private const val FACT = "UA"
        private const val EXPRESSION = "1 + 2"

        private val deserializer = JacksonDeserializer()
    }
}
