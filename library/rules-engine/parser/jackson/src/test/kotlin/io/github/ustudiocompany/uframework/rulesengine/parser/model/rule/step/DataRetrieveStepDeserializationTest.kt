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
internal class DataRetrieveStepDeserializationTest : UnitTest() {

    init {

        "The data retrieve step" - {

            "when condition is present" - {
                val json = """
                    | {
                    |   "type": "dataRetrieve",
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
                    |   "uri": "$URI",
                    |   "args": [
                    |     {
                    |       "name": "$ARG_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$FACT_ARG"
                    |       }
                    |     }
                    |   ],
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
                    result shouldBe StepModel.DataRetrieve(
                        condition = listOf(
                            PredicateModel(
                                target = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT))),
                                operator = OPERATOR_EQ,
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            )
                        ),
                        uri = URI,
                        args = listOf(
                            ArgModel(
                                name = ARG_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(FACT_ARG))
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
                    |   "type": "dataRetrieve",
                    |   "uri": "$URI",
                    |   "args": [
                    |     {
                    |       "name": "$ARG_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$FACT_ARG"
                    |       }
                    |     }
                    |   ],
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
                    result shouldBe StepModel.DataRetrieve(
                        condition = emptyList(),
                        uri = URI,
                        args = listOf(
                            ArgModel(
                                name = ARG_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(FACT_ARG))
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
        private const val FACT_ARG = """SCHEME-1"""
        private const val OPERATOR_EQ = "eq"
        private const val URI = "mdm:entity"
        private const val ARG_NAME = "arg-1"
        private const val ACTION = "put"

        private val deserializer = Deserializer()
    }
}
