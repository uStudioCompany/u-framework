package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.TypeRef
import com.jayway.jsonpath.spi.mapper.MappingException
import com.jayway.jsonpath.spi.mapper.MappingProvider

internal class JsonElementMappingProvider(private val objectMapper: ObjectMapper) : MappingProvider {

    override fun <T> map(source: Any?, targetType: Class<T>, configuration: Configuration): T? {
        if (source == null) return null
        try {
            return objectMapper.convertValue(source, targetType)
        } catch (expected: Exception) {
            throw MappingException(expected)
        }
    }

    override fun <T> map(source: Any?, targetType: TypeRef<T>, configuration: Configuration): T? {
        if (source == null) return null
        try {
            val type = objectMapper.typeFactory.constructType(targetType.type)
            return objectMapper.convertValue(source, type) as T
        } catch (expected: Exception) {
            throw MappingException(expected)
        }
    }
}
