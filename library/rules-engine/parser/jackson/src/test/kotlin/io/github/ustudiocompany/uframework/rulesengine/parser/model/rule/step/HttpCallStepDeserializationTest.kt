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
internal class HttpCallStepDeserializationTest : UnitTest() {

    init {

        "The data retrieve step" - {

            "when condition is present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "httpCall",
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
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$FACT_BODY"
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
                    result shouldBe StepModel.HttpCall(
                        id = STEP_ID,
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
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(FACT_BODY))
                        ),
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }

            "when condition is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "httpCall",
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
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$FACT_BODY"
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
                    result shouldBe StepModel.HttpCall(
                        id = STEP_ID,
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
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(FACT_BODY))
                        ),
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }

            "when args is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "httpCall",
                    |   "uri": "$URI",
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$FACT_BODY"
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
                    result shouldBe StepModel.HttpCall(
                        id = STEP_ID,
                        condition = emptyList(),
                        uri = URI,
                        args = emptyList(),
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(FACT_BODY))
                        ),
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }

            "when body is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "httpCall",
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
                    result shouldBe StepModel.HttpCall(
                        id = STEP_ID,
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
                        body = null,
                        result = ResultModel.Put(source = SOURCE_RESULT)
                    )
                }
            }

            "when result is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "httpCall",
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
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$FACT_BODY"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.HttpCall(
                        id = STEP_ID,
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
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(FACT_BODY))
                        ),
                        result = null
                    )
                }
            }
        }
    }

    private companion object {
        private const val STEP_ID = "step-1"
        private const val SOURCE_RESULT = "variables"
        private const val FACT = """SCHEME-1"""
        private const val FACT_ARG = """SCHEME-2"""
        private const val FACT_BODY = """BODY"""
        private const val OPERATOR_EQ = "eq"
        private const val URI = "mdm:entity"
        private const val ARG_NAME = "arg-1"
        private const val ACTION = "put"

        private val deserializer = Deserializer()
    }
}
