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
internal class MessagePublishStepDeserializationTest : UnitTest() {

    init {

        "The data retrieve step" - {

            "when condition is present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "messagePublish",
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
                    |   "routeKey": {
                    |     "kind": "fact",
                    |     "fact": "$ROUTE_KEY_FACT"
                    |   },
                    |   "headers": [
                    |     {
                    |       "name": "$HEADER_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$HEADER_FACT"
                    |       }
                    |     }
                    |   ],
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$BODY_FACT"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.MessagePublish(
                        id = STEP_ID,
                        condition = listOf(
                            PredicateModel(
                                target = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT))),
                                operator = OPERATOR_EQ,
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            )
                        ),
                        routeKey = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(ROUTE_KEY_FACT))
                        ),
                        headers = listOf(
                            MessageHeaderModel(
                                name = HEADER_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(HEADER_FACT))
                                )
                            )
                        ),
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(BODY_FACT))
                        )
                    )
                }
            }

            "when condition is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "messagePublish",
                    |   "routeKey": {
                    |     "kind": "fact",
                    |     "fact": "$ROUTE_KEY_FACT"
                    |   },
                    |   "headers": [
                    |     {
                    |       "name": "$HEADER_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$HEADER_FACT"
                    |       }
                    |     }
                    |   ],
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$BODY_FACT"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.MessagePublish(
                        id = STEP_ID,
                        condition = emptyList(),
                        routeKey = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(ROUTE_KEY_FACT))
                        ),
                        headers = listOf(
                            MessageHeaderModel(
                                name = HEADER_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(HEADER_FACT))
                                )
                            )
                        ),
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(BODY_FACT))
                        )
                    )
                }
            }

            "when the roure key is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "messagePublish",
                    |   "headers": [
                    |     {
                    |       "name": "$HEADER_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$HEADER_FACT"
                    |       }
                    |     }
                    |   ],
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$BODY_FACT"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.MessagePublish(
                        id = STEP_ID,
                        condition = emptyList(),
                        routeKey = null,
                        headers = listOf(
                            MessageHeaderModel(
                                name = HEADER_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(HEADER_FACT))
                                )
                            )
                        ),
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(BODY_FACT))
                        )
                    )
                }
            }

            "when headers is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "messagePublish",
                    |   "routeKey": {
                    |     "kind": "fact",
                    |     "fact": "$ROUTE_KEY_FACT"
                    |   },
                    |   "body": {
                    |     "kind": "fact",
                    |     "fact": "$BODY_FACT"
                    |   }
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.MessagePublish(
                        id = STEP_ID,
                        condition = emptyList(),
                        routeKey = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(ROUTE_KEY_FACT))
                        ),
                        headers = emptyList(),
                        body = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(BODY_FACT))
                        )
                    )
                }
            }

            "when the body is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "messagePublish",
                    |   "routeKey": {
                    |     "kind": "fact",
                    |     "fact": "$ROUTE_KEY_FACT"
                    |   },
                    |   "headers": [
                    |     {
                    |       "name": "$HEADER_NAME",
                    |       "value": {
                    |         "kind": "fact",
                    |         "fact": "$HEADER_FACT"
                    |       }
                    |     }
                    |   ]
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.MessagePublish(
                        id = STEP_ID,
                        condition = emptyList(),
                        routeKey = ValueModel.Literal(
                            fact = FactModel(JsonElement.Text(ROUTE_KEY_FACT))
                        ),
                        headers = listOf(
                            MessageHeaderModel(
                                name = HEADER_NAME,
                                value = ValueModel.Literal(
                                    fact = FactModel(JsonElement.Text(HEADER_FACT))
                                )
                            )
                        ),
                        body = null
                    )
                }
            }
        }
    }

    private companion object {
        private const val STEP_ID = "step-1"
        private const val FACT = """SCHEME-1"""
        private const val OPERATOR_EQ = "eq"
        private const val ROUTE_KEY_FACT = "SCHEME-1"
        private const val HEADER_NAME = "header-1"
        private const val HEADER_FACT = "header-value"
        private const val BODY_FACT = "{}"

        private val deserializer = Deserializer()
    }
}
