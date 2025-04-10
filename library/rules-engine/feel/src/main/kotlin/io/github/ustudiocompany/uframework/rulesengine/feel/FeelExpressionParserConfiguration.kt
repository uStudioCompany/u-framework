package io.github.ustudiocompany.uframework.rulesengine.feel

import org.camunda.feel.context.FunctionProvider

public fun feelExpressionParserConfiguration(
    customFunctions: Iterable<FeelFunction>
): FeelExpressionParserConfiguration =
    FeelExpressionParserConfiguration(customFunctions)

public class FeelExpressionParserConfiguration(customFunctions: Iterable<FeelFunction>) {
    public val functionProvider: FunctionProvider = FeelFunctionProvider(customFunctions)
}
