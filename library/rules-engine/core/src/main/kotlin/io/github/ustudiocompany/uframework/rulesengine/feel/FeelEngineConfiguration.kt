package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.ustudiocompany.uframework.rulesengine.feel.function.FeelFunctionProvider
import io.github.ustudiocompany.uframework.rulesengine.feel.function.defaultFunctionRegistry
import org.camunda.feel.context.FunctionProvider

public class FeelEngineConfiguration(
    public val functionProvider: FunctionProvider = FeelFunctionProvider(defaultFunctionRegistry())
)
