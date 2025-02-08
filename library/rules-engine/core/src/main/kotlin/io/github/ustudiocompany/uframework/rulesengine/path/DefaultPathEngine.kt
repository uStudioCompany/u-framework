package io.github.ustudiocompany.uframework.rulesengine.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Option

public fun defaultPathEngine(mapper: ObjectMapper, vararg options: Option): PathEngine =
    PathEngine(defaultPathEngineConfiguration(mapper, options.toSet()))
