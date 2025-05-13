package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.json.element.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.json.element.merge.strategy.path.AttributePath
import io.github.ustudiocompany.uframework.json.element.merge.strategy.role.MergeRule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest

@OptIn(AirfluxTypesExperimental::class)
internal class MergeStrategyParserTest : UnitTest() {

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
                        |     "$PERSONS": {
                        |       "rule": {
                        |         "name": "mergeByAttributes",
                        |         "attributes": ["$PERSON_ID"]
                        |       },
                        |       "properties": {
                        |         "$ROLES": {
                        |           "rule": {
                        |             "name": "wholeListMerge"
                        |           }
                        |         }
                        |       }
                        |     },
                        |     "$LOCATION": {
                        |       "properties": {
                        |         "$IDENTIFIERS": {
                        |           "rule": {
                        |             "name": "wholeListMerge"
                        |           }
                        |         }
                        |       }
                        |     }
                        |   }
                        | }
                    """.trimMargin(),
                    expected = mapOf(
                        AttributePath(ADDITION_CLASSIFICATIONS) to MergeRule.WholeListMerge,
                        AttributePath(PERSONS) to MergeRule.MergeByAttributes(listOf(PERSON_ID)),
                        AttributePath(PERSONS, ROLES) to MergeRule.WholeListMerge,
                        AttributePath(LOCATION, IDENTIFIERS) to MergeRule.WholeListMerge
                    ),
                )
            ).forEach { (description, json, expected) ->

                description - {
                    val result = parser.parse(json)

                    "then should be return an expected value" {
                        result shouldBeSuccess expected
                    }
                }
            }
        }
    }

    private companion object {
        private const val VERSION = "1.0"
        private const val ADDITION_CLASSIFICATIONS = "additionClassifications"
        private const val PERSONS = "persons"
        private const val PERSON_ID = "id"
        private const val ROLES = "roles"
        private const val LOCATION = "location"
        private const val IDENTIFIERS = "identifiers"

        private val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
            .build()
            .apply {
                registerKotlinModule()
            }

        private val parser = mergeStrategyParser(mapper)
    }

    private data class TestData(val description: String, val json: String, val expected: MergeStrategy)
}
