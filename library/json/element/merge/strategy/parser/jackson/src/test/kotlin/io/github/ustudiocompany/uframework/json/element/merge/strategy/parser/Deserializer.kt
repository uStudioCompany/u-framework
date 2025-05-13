package io.github.ustudiocompany.uframework.json.element.merge.strategy.parser

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

internal class Deserializer {

    private val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
        .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
        .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
        .build()
        .apply {
            registerKotlinModule()
        }

    fun <T> deserialize(input: String, type: Class<T>): T =
        mapper.readValue(input, type)

    fun <T> deserialize(input: String, type: TypeReference<T>): T =
        mapper.readValue(input, type)
}
