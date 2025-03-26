package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun defaultPathEngineConfiguration(mapper: ObjectMapper, vararg options: Option): Configuration =
    defaultPathEngineConfiguration(mapper, options.toSet())

public fun defaultPathEngineConfiguration(mapper: ObjectMapper, options: Set<Option>): Configuration {
    val jsonProvider: JsonProvider =
        DataElementProvider(mapper, mapper.reader().forType(DataElement::class.java))

    val mappingProvider: MappingProvider = DataElementMappingProvider(mapper)

    return Configuration.builder()
        .jsonProvider(jsonProvider)
        .mappingProvider(mappingProvider)
        .options(options)
        .build()
}
