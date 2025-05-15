package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.Deserializer
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class PropertyModelDeserializationTest : UnitTest() {

    init {

        "The PropertyModel type" - {

            listOf(
                TestData(
                    description = "when a role is present on the top level",
                    json = """
                        | {
                        |   "rule": { 
                        |     "name": "wholeListMerge" 
                        |   }
                        | }
                    """.trimMargin(),
                    expected = PropertyModel(rule = RuleModel.WholeListMerge)
                ),
                TestData(
                    description = "when a role is missing on the top level",
                    json = """
                        | {
                        |   "properties": {
                        |     "$FIRST_ATTRIBUTE": {
                        |       "rule": {
                        |         "name": "wholeListMerge"
                        |       }
                        |     }
                        |   }
                        | }
                    """.trimMargin(),
                    expected = PropertyModel(properties = mapOf(FIRST_ATTRIBUTE to PropertyModel(rule = RuleModel.WholeListMerge)))
                ),
                TestData(
                    description = "when a role present on all levels",
                    json = """
                        | {
                        |   "rule": { 
                        |     "name": "wholeListMerge" 
                        |   },
                        |   "properties": {
                        |     "$FIRST_ATTRIBUTE": {
                        |       "rule": {
                        |         "name": "wholeListMerge"
                        |       }
                        |     }
                        |   }
                        | }
                    """.trimMargin(),
                    expected = PropertyModel(
                        rule = RuleModel.WholeListMerge,
                        properties = mapOf(FIRST_ATTRIBUTE to PropertyModel(rule = RuleModel.WholeListMerge))
                    )
                )
            ).forEach { (description, json, expected) ->

                description - {
                    val result = deserializer.deserialize(json, PropertyModel::class.java)

                    "then should be return an expected value" {
                        result shouldBe expected
                    }
                }
            }
        }
    }

    private companion object {
        private const val FIRST_ATTRIBUTE = "additionClassifications"

        private val deserializer = Deserializer()
    }

    private data class TestData(val description: String, val json: String, val expected: PropertyModel)
}
