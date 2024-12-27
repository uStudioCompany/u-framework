package io.github.ustudiocompany.uframework.rulesengine.core.path

import com.fasterxml.jackson.databind.ObjectMapper

public fun defaultPathCompiler(mapper: ObjectMapper): Path.Compiler =
    Path.Compiler(defaultPathCompilerConfiguration(mapper))
