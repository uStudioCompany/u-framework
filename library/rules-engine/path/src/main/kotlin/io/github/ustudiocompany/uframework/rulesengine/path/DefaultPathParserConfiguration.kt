package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun defaultPathParserConfiguration(mapper: ObjectMapper, vararg options: Option): Configuration =
    defaultPathParserConfiguration(mapper, options.toSet())

public fun defaultPathParserConfiguration(mapper: ObjectMapper, options: Set<Option>): Configuration {
    val jsonProvider: JsonProvider =
        JsonElementProvider(mapper, mapper.reader().forType(JsonElement::class.java))

    val mappingProvider: MappingProvider = JsonElementMappingProvider(mapper)

    return Configuration.builder()
        .jsonProvider(jsonProvider)
        .mappingProvider(mappingProvider)
        .options(options)
        .build()
}
