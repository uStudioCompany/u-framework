package io.github.ustudiocompany.uframework.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun defaultPathCompilerConfiguration(mapper: ObjectMapper, vararg options: Option): Configuration =
    defaultPathCompilerConfiguration(mapper, options.toSet())

public fun defaultPathCompilerConfiguration(mapper: ObjectMapper, options: Set<Option>): Configuration {
    val jsonProvider: JsonProvider =
        DataElementProvider(mapper, mapper.reader().forType(DataElement::class.java))

    val mappingProvider: MappingProvider = DataElementMappingProvider(mapper)

    return Configuration.builder()
        .jsonProvider(jsonProvider)
        .mappingProvider(mappingProvider)
        .options(options)
        .build()
}
