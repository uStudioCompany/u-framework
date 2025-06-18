package io.github.ustudiocompany.uframework.json.element.merge

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.matcher.shouldContainSuccessInstance
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.element.merge.strategy.parser.mergeStrategyParser
import io.github.ustudiocompany.uframework.json.element.parser.jackson.module.JsonElementModule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.matchers.shouldBe

@OptIn(AirfluxTypesExperimental::class)
internal class MergeTest : UnitTest() {

    init {

        "Testing the merge function" {
            val strategy = shouldBeSuccess { parser.parse(STRATEGY) }
            val dst = mapper.readValue(DST, JsonElement::class.java)
            val source = mapper.readValue(SOURCE, JsonElement::class.java)
            val expected = mapper.readValue(EXPECTED_RESULT, JsonElement::class.java)
            val result = dst.merge(source, strategy)

            val actual = result.shouldContainSuccessInstance()
            actual shouldBe expected
        }
    }

    private companion object {
        private val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
            .build()
            .apply {
                registerKotlinModule()
                registerModule(JsonElementModule())
            }

        private val parser = mergeStrategyParser(mapper)

        private const val STRATEGY = """
            {
  "version": "0.0.1",
  "properties": {
    "identifiers": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "scheme"
        ]
      }
    },
    "title": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "lang"
        ]
      }
    },
    "description": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "lang"
        ]
      }
    },
    "parties": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "id"
        ]
      },
      "properties": {
        "roles": {
          "rule": {
            "name": "wholeListMerge"
          }
        }
      }
    },
    "additionalClassifications": {
      "rule": {
        "name": "wholeListMerge"
      }
    },
    "locations": {
      "rule": {
        "name": "wholeListMerge"
      },
      "properties": {
        "gazetteer": {
          "properties": {
            "identifiers": {
              "rule": {
                "name": "wholeListMerge"
              }
            }
          }
        }
      }
    },
    "documents": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "id"
        ]
      }
    },
    "relatedProcesses": {
      "rule": {
        "name": "mergeByAttributes",
        "attributes": [
          "scheme, identifier, relationship"
        ]
      }
    }
  }
}
        """

        private val DST = """
            | {  
            |   "id": "b46782d1-9a32-41e8-a248-34fe39534f60",
            |   "identifiers": [
            |     {
            |       "id": "DREAM-UA-18062025-F6837F1A",
            |       "scheme": "UA-DREAM"
            |     },
            |     {
            |       "id": "DREAM-UA-18062025-F6837F1A",
            |       "scheme": "OC4IDS"
            |     }
            |   ]
            | }
        """.trimMargin()

        private val SOURCE = """
            | {
            |   "date": "2025-06-18T16:22:59Z",
            |   "dateModified": "2025-06-18T16:17:48Z",
            |   "status": "draft",
            |   "release": "019783d9-e034-7415-a201-c8d151896710"
            | }
        """.trimMargin()

        private val EXPECTED_RESULT = """
            | {  
            |    "id": "b46782d1-9a32-41e8-a248-34fe39534f60",
            |    "identifiers": [
            |     {
            |       "id": "DREAM-UA-18062025-F6837F1A",
            |       "scheme": "UA-DREAM"
            |     },
            |     {
            |       "id": "DREAM-UA-18062025-F6837F1A",
            |       "scheme": "OC4IDS"
            |     }
            |   ],
            |   "date": "2025-06-18T16:22:59Z",
            |   "dateModified": "2025-06-18T16:17:48Z",
            |   "status": "draft",
            |   "release": "019783d9-e034-7415-a201-c8d151896710"
            | }
        """.trimMargin()
    }
}
