package io.github.ustudiocompany.uframework.rulesengine.feel

import org.camunda.feel.context.FunctionProvider

public class FeelEngineConfiguration(functionRegistry: Iterable<FeelFunction>) {
    public val functionProvider: FunctionProvider = FeelFunctionProvider(functionRegistry)
}
