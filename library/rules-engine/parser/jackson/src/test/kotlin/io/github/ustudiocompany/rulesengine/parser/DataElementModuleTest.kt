package io.github.ustudiocompany.rulesengine.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.parser.DataElementModule
import io.github.ustudiocompany.uframework.test.kotest.UnitTest
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

internal class DataElementModuleTest : UnitTest() {

    init {

        "The Jackson module for deserializing JSON to the DataElement type" - {

            withData(
                nameFn = { "from json: '${it.first}'" },
                testData()
            ) { (json, data) ->
                json.deserialization() shouldBe data
            }
        }
    }

    private fun testData() = listOf(
        """null""" to DataElement.Null,
        """true""" to DataElement.Bool.asTrue,
        """false""" to DataElement.Bool.asFalse,
        """"user-1"""" to DataElement.Text("user-1"),
        """123""" to DataElement.Decimal(BigDecimal("123")),
        """123.45""" to DataElement.Decimal(BigDecimal("123.45")),
        """{}""" to DataElement.Struct(),
        """[]""" to DataElement.Array(),
        """[[],[]]""" to DataElement.Array(DataElement.Array(), DataElement.Array()),
        """{"id":null}""" to DataElement.Struct("id" to DataElement.Null),
        """{"id":123}""" to DataElement.Struct("id" to DataElement.Decimal(BigDecimal("123"))),
        """{"name":"user-1"}""" to DataElement.Struct("name" to DataElement.Text("user-1")),
        """{"isActive":true}""" to DataElement.Struct("isActive" to DataElement.Bool.asTrue),
        """{"isActive":false}""" to DataElement.Struct("isActive" to DataElement.Bool.asFalse),
        """{"user":{}}""" to DataElement.Struct("user" to DataElement.Struct()),
        """{"users":[]}""" to DataElement.Struct("users" to DataElement.Array()),
        """{"users":[{"id":123}]}""" to DataElement.Struct(
            "users" to DataElement.Array(
                DataElement.Struct("id" to DataElement.Decimal(BigDecimal("123")))
            )
        ),
        """[null,true,false,"user-1",123,123.45]""" to DataElement.Array(
            DataElement.Null,
            DataElement.Bool.asTrue,
            DataElement.Bool.asFalse,
            DataElement.Text("user-1"),
            DataElement.Decimal(BigDecimal("123")),
            DataElement.Decimal(BigDecimal("123.45"))
        )
    )

    companion object {
        private val mapper = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(DataElementModule())
        }

        private fun String.deserialization(): DataElement = mapper.readValue(this, DataElement::class.java)
    }
}
