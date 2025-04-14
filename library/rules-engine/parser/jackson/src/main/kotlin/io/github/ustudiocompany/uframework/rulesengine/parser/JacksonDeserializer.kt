package io.github.ustudiocompany.uframework.rulesengine.parser

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.parser.module.DataElementModule
import io.github.ustudiocompany.uframework.rulesengine.parser.module.FactModule

internal class JacksonDeserializer {

    private val mapper: JsonMapper = JsonMapper.builder(JsonFactory())
        .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
        .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true)
        .build()
        .apply {
            registerKotlinModule()
            registerModules(DataElementModule())
            registerModules(FactModule())
        }

    fun <T> deserialize(input: String, type: Class<T>): ResultK<T, Errors> =
        try {
            mapper.readValue(input, type).asSuccess()
        } catch (expected: Exception) {
            Errors.Deserialization(expected).asFailure()
        }

    fun <T> deserialize(input: String, type: TypeReference<T>): ResultK<T, Errors> =
        try {
            mapper.readValue(input, type).asSuccess()
        } catch (expected: Exception) {
            Errors.Deserialization(expected).asFailure()
        }

    sealed class Errors : Failure {

        class Deserialization(exception: Exception) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of deserialization JSON with rules."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
        }

        private companion object {
            private const val PREFIX = "RULES-DESERIALIZER-"
        }
    }
}
