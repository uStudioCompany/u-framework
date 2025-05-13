package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.model

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.Deserializer
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class RuleModelDeserializationTest : UnitTest() {

    init {

        "The RuleModel type" - {

            listOf(
                TestData(
                    role = "wholeListMerge",
                    json = """
                        | {
                        |   "name": "wholeListMerge"
                        | }
                    """.trimMargin(),
                    expected = RuleModel.WholeListMerge
                ),
                TestData(
                    role = "mergeByAttribute",
                    json = """
                        | {
                        |   "name": "mergeByAttribute",
                        |   "attributes": ["$FIRST_ATTRIBUTE", "$SECOND_ATTRIBUTE"]
                        | }
                    """.trimMargin(),
                    expected = RuleModel.MergeByAttribute(listOf(FIRST_ATTRIBUTE, SECOND_ATTRIBUTE))
                )
            ).forEach { (role, json, expected) ->
                "when role name is $role" - {

                    val result = deserializer.deserialize(json, RuleModel::class.java)

                    "then should be return an expected value" {
                        result shouldBe expected
                    }
                }
            }
        }
    }

    private companion object {
        private const val FIRST_ATTRIBUTE = "id"
        private const val SECOND_ATTRIBUTE = "name"

        private val deserializer = Deserializer()
    }

    private data class TestData(val role: String, val json: String, val expected: RuleModel)
}
