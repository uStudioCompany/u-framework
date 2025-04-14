package io.github.ustudiocompany.rulesengine.parser.module

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.parser.model.rule.FactModel
import io.github.ustudiocompany.uframework.rulesengine.parser.module.FactModule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class FactModuleTest : UnitTest() {

    init {

        "The Jackson module for deserializing JSON to the FactModel type" - {

            "when the value of a fact is acceptable" - {
                withData(
                    nameFn = { "value: '${it.first}'" },
                    acceptableTestData()
                ) { (json, data) ->
                    json.deserialization() shouldBe TestData(fact = FactModel(data))
                }
            }

            "when the value of a fact is not acceptable" - {
                withData(
                    nameFn = { "value: '$it'" },
                    notAcceptableTestData()
                ) { json ->
                    shouldThrow<JsonProcessingException> {
                        json.deserialization()
                    }
                }
            }
        }
    }

    private fun acceptableTestData() = listOf(
        """{"fact": null}""" to DataElement.Null,
        """{"fact": true}""" to DataElement.Bool.asTrue,
        """{"fact": false}""" to DataElement.Bool.asFalse,
        """{"fact": "user-1"}""" to DataElement.Text("user-1"),
        """{"fact": 123}""" to DataElement.Decimal(BigDecimal("123")),
        """{"fact": 123.45}""" to DataElement.Decimal(BigDecimal("123.45")),
        """{"fact": []}""" to DataElement.Array(),
        """{"fact": [null,true,false,"user-1",123,123.45]}""" to DataElement.Array(
            DataElement.Null,
            DataElement.Bool.asTrue,
            DataElement.Bool.asFalse,
            DataElement.Text("user-1"),
            DataElement.Decimal(BigDecimal("123")),
            DataElement.Decimal(BigDecimal("123.45"))
        )
    )

    private fun notAcceptableTestData() = listOf(
        """{"fact": [{}]}""",
        """{"fact": [[],[]]}""",
        """{"fact": [[true, null],[null, false]]}""",
        """{"fact": {"id":null}}""",
        """{"fact": {"id":123}}""",
        """{"fact": {"name":"user-1"}}""",
        """{"fact": {"isActive":true}}""",
        """{"fact": {"isActive":false}}""",
    )

    companion object {
        private val mapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(FactModule())
        }

        private fun String.deserialization(): TestData = mapper.readValue(this, TestData::class.java)
    }

    private data class TestData(
        @JsonProperty("fact") val fact: FactModel
    )
}
