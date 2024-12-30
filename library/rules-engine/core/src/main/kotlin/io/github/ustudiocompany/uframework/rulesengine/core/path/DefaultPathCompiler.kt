package io.github.ustudiocompany.uframework.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.Option

public fun defaultPathCompiler(mapper: ObjectMapper, vararg options: Option): Path.Compiler =
    Path.Compiler(defaultPathCompilerConfiguration(mapper, options.toSet()))
