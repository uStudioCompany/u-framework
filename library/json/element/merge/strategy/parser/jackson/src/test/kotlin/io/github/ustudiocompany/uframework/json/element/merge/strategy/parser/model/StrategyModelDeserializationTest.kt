package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.Deserializer
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class StrategyModelDeserializationTest : UnitTest() {

    init {

        "The StrategyModel type" - {

            listOf(
                TestData(
                    description = "when the strategy description format is valid",
                    json = """
                        | {
                        |   "version": "$VERSION",
                        |   "properties": {
                        |     "$ADDITION_CLASSIFICATIONS": {
                        |       "rule": { 
                        |         "name": "wholeListMerge" 
                        |       }
                        |     },
                        |     "$DOCUMENTS": {
                        |       "rule": {
                        |         "name": "mergeByAttributes",
                        |         "attributes": ["$DOCUMENT_ID"]
                        |       }
                        |     }
                        |   }
                        | }
                    """.trimMargin(),
                    expected = StrategyModel(
                        version = VERSION,
                        properties = mapOf(
                            ADDITION_CLASSIFICATIONS to PropertyModel(rule = RuleModel.WholeListMerge),
                            DOCUMENTS to PropertyModel(
                                rule = RuleModel.MergeByAttributes(listOf(DOCUMENT_ID))
                            )
                        )
                    )
                )
            ).forEach { (description, json, expected) ->

                description - {
                    val result = deserializer.deserialize(json, StrategyModel::class.java)

                    "then should be return an expected value" {
                        result shouldBe expected
                    }
                }
            }
        }
    }

    private companion object {
        private const val VERSION = "1.0"
        private const val ADDITION_CLASSIFICATIONS = "additionClassifications"
        private const val DOCUMENTS = "documents"
        private const val DOCUMENT_ID = "id"

        private val deserializer = Deserializer()
    }

    private data class TestData(val description: String, val json: String, val expected: StrategyModel)
}
