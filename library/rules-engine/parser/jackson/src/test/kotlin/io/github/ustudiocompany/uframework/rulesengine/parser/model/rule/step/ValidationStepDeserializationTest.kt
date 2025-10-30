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
internal class ValidationStepDeserializationTest : UnitTest() {

    init {

        "The validation step type" - {

            "when condition is present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "validation",
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
                    |   "target": {
                    |     "kind": "reference",
                    |     "source": "$SOURCE_INPUT",
                    |     "path": "$PATH_INPUT"
                    |   },
                    |   "operator": "$OPERATOR_IN",
                    |   "value": {
                    |     "kind": "reference",
                    |     "source": "$SOURCE_VARS",
                    |     "path": "$PATH_VARS"
                    |   },
                    |   "errorCode": "$ERROR_CODE"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.Validation(
                        id = STEP_ID,
                        condition = listOf(
                            PredicateModel(
                                target = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT))),
                                operator = OPERATOR_EQ,
                                value = ValueModel.Literal(fact = FactModel(JsonElement.Text(FACT)))
                            )
                        ),
                        target = ValueModel.Reference(source = SOURCE_INPUT, path = PATH_INPUT),
                        operator = OPERATOR_IN,
                        value = ValueModel.Reference(source = SOURCE_VARS, path = PATH_VARS),
                        errorCode = ERROR_CODE
                    )
                }
            }

            "when condition is not present" - {
                val json = """
                    | {
                    |   "id": "$STEP_ID",
                    |   "type": "validation",
                    |   "target": {
                    |     "kind": "reference",
                    |     "source": "$SOURCE_INPUT",
                    |     "path": "$PATH_INPUT"
                    |   },
                    |   "operator": "$OPERATOR_IN",
                    |   "value": {
                    |     "kind": "reference",
                    |     "source": "$SOURCE_VARS",
                    |     "path": "$PATH_VARS"
                    |   },
                    |   "errorCode": "$ERROR_CODE"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(
                    input = json,
                    type = StepModel::class.java
                )

                "then should be return valid type" {
                    result shouldBe StepModel.Validation(
                        id = STEP_ID,
                        condition = emptyList(),
                        target = ValueModel.Reference(source = SOURCE_INPUT, path = PATH_INPUT),
                        operator = OPERATOR_IN,
                        value = ValueModel.Reference(source = SOURCE_VARS, path = PATH_VARS),
                        errorCode = ERROR_CODE
                    )
                }
            }
        }
    }

    private companion object {
        private const val STEP_ID = "step-1"
        private const val SOURCE_INPUT = "input_body"
        private const val SOURCE_VARS = "variables"
        private const val PATH_INPUT = "$.scheme"
        private const val PATH_VARS = "$.[*].scheme"
        private const val FACT = """SCHEME-1"""
        private const val OPERATOR_EQ = "eq"
        private const val OPERATOR_IN = "in"
        private const val ERROR_CODE = "err-1"

        private val deserializer = Deserializer()
    }
}
