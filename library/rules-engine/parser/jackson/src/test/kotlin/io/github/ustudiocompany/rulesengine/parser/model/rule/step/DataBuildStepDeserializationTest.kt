package io.github.ustudiocompany.rulesengine.parser.model.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.parser.JacksonDeserializer
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.ValueModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.condition.PredicateModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.DataSchemaModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.ResultModel
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step.StepModel
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(AirfluxTypesExperimental::class)
internal class DataBuildStepDeserializationTest : UnitTest() {

    init {

        "The data build step" - {

            "when condition is present" - {
                val json = """
                    | {
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
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<StepModel.DataBuild>()
                    value shouldBe StepModel.DataBuild(
                        condition = listOf(
                            PredicateModel(
                                target = ValueModel.Literal(fact = FactModel(DataElement.Text(FACT))),
                                operator = OPERATOR_EQ,
                                value = ValueModel.Literal(fact = FactModel(DataElement.Text(FACT)))
                            )
                        ),
                        dataSchema = DataSchemaModel.Struct(
                            properties = listOf(
                                DataSchemaModel.StructProperty.Element(
                                    name = PROP_NAME,
                                    value = ValueModel.Literal(
                                        fact = FactModel(DataElement.Text(PROP_VALUE))
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
                    val value = result.shouldContainSuccessInstance()
                        .shouldBeInstanceOf<StepModel.DataBuild>()
                    value shouldBe StepModel.DataBuild(
                        condition = emptyList(),
                        dataSchema = DataSchemaModel.Struct(
                            properties = listOf(
                                DataSchemaModel.StructProperty.Element(
                                    name = PROP_NAME,
                                    value = ValueModel.Literal(
                                        fact = FactModel(DataElement.Text(PROP_VALUE))
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
        private const val SOURCE_RESULT = "variables"
        private const val FACT = """SCHEME-1"""
        private const val OPERATOR_EQ = "eq"
        private const val PROP_NAME = "prop-1"
        private const val PROP_VALUE = """SCHEME-1"""
        private const val ACTION = "put"

        private val deserializer = JacksonDeserializer()
    }
}
