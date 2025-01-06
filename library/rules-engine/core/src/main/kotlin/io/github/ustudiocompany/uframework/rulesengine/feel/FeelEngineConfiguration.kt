package io.github.ustudiocompany.uframework.rulesengine.feel

import org.camunda.feel.context.FunctionProvider

public class FeelEngineConfiguration(
    public val functionProvider: FunctionProvider = FeelFunctionProvider(defaultFunctionRegistry())
)
