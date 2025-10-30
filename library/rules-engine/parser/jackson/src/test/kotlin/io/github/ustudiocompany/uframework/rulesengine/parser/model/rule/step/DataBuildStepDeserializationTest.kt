package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.Deserializer
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.PredicateModel
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class DataBuildStepDeserializationTest : UnitTest() {

    init {

        "The data build step" - {

            "when condition is present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "dataBuild",
                    |   "condition": [
                    |     {
                    |       "target": {
                    |         "kind": "fact",
                    |         "fact": "$FACT"
                    |       },
                    |       "operator": "$OPERATOR_EQ",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$FACT"
                    |       }
                    |     }
                    |   ],
                    |   "dataSchema": {
                    |     "type": "struct",
                    |     "properties": [
                    |       {
                    |         "type": "value",
                    |         "name": "$PROP_NAME",
                    |         "value": {
                    |           "kind": "fact",
                    |           "fact": "$PROP_VALUE"
                    |         }
                    |       }
                    |     ]
                    |   },
                    |   "result": {
                    |     "source" : "$SOURCE_RESULT",
                    |     "action" : "$ACTION"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.DataBuild(
                        id = STEP_ID,
                        condition = listOf(
                            PredicateModel(
                                target = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT))),
                                operator = OPERATOR_EQ,
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            )
                        ),
                        dataSchema = DataSchemaModel.Struct(
                            properties = listOf(
                                DataSchemaModel.StructProperty.Element(
                                    name = PROP_NAME,
                                    value = ValueModel.Literal(
                                        fact = FactModel(JsonElement.Text(PROP_VALUE))
                                    )
                                )
                            )
                        ),
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }

            "when condition is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "dataBuild",
                    |   "dataSchema": {
                    |     "type": "struct",
                    |     "properties": [
                    |       {
                    |         "type": "value",
                    |         "name": "$PROP_NAME",
                    |         "value": {
                    |           "kind": "fact",
                    |           "fact": "$PROP_VALUE"
                    |         }
                    |       }
                    |     ]
                    |   },
                    |   "result": {
                    |     "source" : "$SOURCE_RESULT",
                    |     "action" : "$ACTION"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.DataBuild(
                        id = STEP_ID,
                        condition = emptyList(),
                        dataSchema = DataSchemaModel.Struct(
                            properties = listOf(
                                DataSchemaModel.StructProperty.Element(
                                    name = PROP_NAME,
                                    value = ValueModel.Literal(
                                        fact = FactModel(JsonElement.Text(PROP_VALUE))
                                    )
                                )
                            )
                        ),
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }
        }
    }

    private companion object {
        private const val STEP_ID = "step-1"
        private const val SOURCE_RESULT = "variables"
        private const val FACT = """SCHEME-1"""
        private const val OPERATOR_EQ = "eq"
        private const val PROP_NAME = "prop-1"
        private const val PROP_VALUE = """SCHEME-1"""
        private const val ACTION = "put"

        private val deserializer = Deserializer()
    }
}
