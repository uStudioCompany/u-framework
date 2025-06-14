package io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.step

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.rulesengine.parser.Deserializer
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class StepResultModelDeserializationTest : UnitTest() {

    init {

        "The StepResultModel type" - {

            "when action is put" - {
                val json = """
                    | {
                    |   "source": "$SOURCE",
                    |   "action": "put"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ResultModel::class.java)

                "then should be return a literal value" {
                    result shouldBe ResultModel.Put(source = SOURCE)
                }
            }

            "when action is replace" - {
                val json = """
                    | {
                    |   "source": "$SOURCE",
                    |   "action": "replace"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ResultModel::class.java)

                "then should be return a reference value" {
                    result shouldBe ResultModel.Replace(source = SOURCE)
                }
            }

            "when action is merge" - {
                val json = """
                    | {
                    |   "source": "$SOURCE",
                    |   "action": "merge",
                    |   "mergeStrategyCode": "$MERGE_STRATEGY_CODE"
                    | }
                """.trimMargin()

                val result = deserializer.deserialize(json, ResultModel::class.java)

                "then should be return an expression value" {
                    result shouldBe ResultModel.Merge(source = SOURCE, mergeStrategyCode = MERGE_STRATEGY_CODE)
                }
            }
        }
    }

    private companion object {
        private const val SOURCE = "input_body"
        private const val MERGE_STRATEGY_CODE = "merge-strategy-code"

        private val deserializer = Deserializer()
    }
}
