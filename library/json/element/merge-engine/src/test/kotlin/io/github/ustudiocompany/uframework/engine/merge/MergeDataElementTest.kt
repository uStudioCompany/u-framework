package io.github.ustudiocompany.uframework.engine.merge

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.engine.merge.path.AttributePath
import io.github.ustudiocompany.uframework.engine.merge.strategy.MergeStrategy
import io.github.ustudiocompany.uframework.engine.merge.strategy.role.MergeRule
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.parser.module.JsonElementModule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest

@OptIn(AirfluxTypesExperimental::class)
internal class MergeDataElementTest : UnitTest() {

    init {

        "The MergeByAttribute strategy" - {
            val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
                .build()
                .apply {
                    registerKotlinModule()
                    registerModules(JsonElementModule())
                }

            val strategy: MergeStrategy = mapOf(
                AttributePath("identifierMerge") to MergeRule.MergeByAttributes(listOf("id")),
                AttributePath("identifierMerge", "subarray") to MergeRule.MergeByAttributes(listOf("id")),
                AttributePath("identifierMerge", "arrayForReplace") to MergeRule.WholeListMerge,
                AttributePath("roles") to MergeRule.WholeListMerge
            )

            val dst = mapper.readValue(DST, JsonElement::class.java)
            val src = mapper.readValue(SRC, JsonElement::class.java)
            val merged = mapper.readValue(MERGED_RESULT, JsonElement::class.java)

            val result = dst.merge(src, strategy)

            "should merge the two data elements" {
                result shouldBeSuccess merged
            }
        }
    }

    private companion object {

        private val DST = """
            | {
            |   "forDeleteBool1": true,
            |   "forReplaceBool": true,
            |   "ocid": "ocds-213czf-1",
            |   "id": "1",
            |   "date": "2000-01-01T00:00:00Z",
            |   "identifierMerge": [
            |     {
            |       "id": 1,
            |       "forDeleteBool2": true,
            |       "subarray": [
            |         {
            |           "id": 10,
            |           "forDeleteBool3": true
            |         }
            |       ],
            |       "arrayForReplace": [
            |         1, 2, 3  
            |       ]
            |     }
            |   ],
            |   "roles": [
            |     "admin"
            |   ]
            | }
        """.trimMargin()

        private val SRC = """
            | {
            |   "forDeleteBool1": null,
            |   "forReplaceBool": false,
            |   "ocid": "ocds-213czf-1",
            |   "id": "2",
            |   "date": "2000-01-02T00:00:00Z",
            |   "identifierMerge": [
            |     {
            |       "id": 1,
            |       "forDeleteBool2": null,
            |       "subarray": [
            |         {
            |           "id": 10,
            |           "forDeleteBool3": null
            |         },
            |         {
            |           "id": 11,
            |           "forDeleteBool4": null
            |         }
            |       ],
            |       "arrayForReplace": [
            |         4, 5
            |       ]       
            |     },
            |     {
            |       "id": 2,
            |       "subarray": [
            |         {
            |           "id": 12
            |         }
            |       ]
            |     }
            |   ],
            |   "roles": [
            |     "admin", "user"
            |   ]
            | }            
        """.trimMargin()

        private val MERGED_RESULT = """
            | {
            |   "forReplaceBool": false,
            |   "ocid": "ocds-213czf-1",
            |   "id": "2",
            |   "date": "2000-01-02T00:00:00Z",
            |   "identifierMerge": [
            |     {
            |       "id": 1,
            |       "subarray": [
            |         {
            |           "id": 10
            |         },
            |         {
            |           "id": 11
            |         }
            |       ],
            |       "arrayForReplace": [
            |         4, 5
            |       ]
            |     },
            |     {
            |       "id": 2,
            |       "subarray": [
            |         {
            |           "id": 12
            |         }
            |       ]
            |     }
            |   ],
            |   "roles": [
            |     "admin", "user"
            |   ]
            | }            
        """.trimMargin()
    }
}
