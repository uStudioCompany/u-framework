package io.github.ustudiocompany.uframework.json.element.parser.jackson.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class JsonElementModuleTest : UnitTest() {

    init {

        "The Jackson module for deserializing JSON to the JsonElement type" - {

            withData(
                nameFn = { "from json: '${it.first}'" },
                testData()
            ) { (json, data) ->
                json.deserialization() shouldBe data
            }
        }
    }

    private fun testData() = listOf(
        """null""" to JsonElement.Null,
        """true""" to JsonElement.Bool.asTrue,
        """false""" to JsonElement.Bool.asFalse,
        """"user-1"""" to JsonElement.Text("user-1"),
        """123""" to JsonElement.Decimal(BigDecimal("123")),
        """123.45""" to JsonElement.Decimal(BigDecimal("123.45")),
        """{}""" to JsonElement.Struct(),
        """[]""" to JsonElement.Array(),
        """[[],[]]""" to JsonElement.Array(JsonElement.Array(), JsonElement.Array()),
        """{"id":null}""" to JsonElement.Struct("id" to JsonElement.Null),
        """{"id":123}""" to JsonElement.Struct("id" to JsonElement.Decimal(BigDecimal("123"))),
        """{"name":"user-1"}""" to JsonElement.Struct("name" to JsonElement.Text("user-1")),
        """{"isActive":true}""" to JsonElement.Struct("isActive" to JsonElement.Bool.asTrue),
        """{"isActive":false}""" to JsonElement.Struct("isActive" to JsonElement.Bool.asFalse),
        """{"user":{}}""" to JsonElement.Struct("user" to JsonElement.Struct()),
        """{"users":[]}""" to JsonElement.Struct("users" to JsonElement.Array()),
        """{"users":[{"id":123}]}""" to JsonElement.Struct(
            "users" to JsonElement.Array(
                JsonElement.Struct("id" to JsonElement.Decimal(BigDecimal("123")))
            )
        ),
        """[null,true,false,"user-1",123,123.45]""" to JsonElement.Array(
            JsonElement.Null,
            JsonElement.Bool.asTrue,
            JsonElement.Bool.asFalse,
            JsonElement.Text("user-1"),
            JsonElement.Decimal(BigDecimal("123")),
            JsonElement.Decimal(BigDecimal("123.45"))
        )
    )

    companion object {
        private val mapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JsonElementModule())
        }

        private fun String.deserialization(): JsonElement = mapper.readValue(this, JsonElement::class.java)
    }
}
