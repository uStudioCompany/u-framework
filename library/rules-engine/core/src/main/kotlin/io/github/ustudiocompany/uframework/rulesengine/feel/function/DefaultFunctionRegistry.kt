package io.github.ustudiocompany.uframework.rulesengine.feel.function

internal fun defaultFunctionRegistry(): Iterable<FeelFunction> = listOf(
    UuidGenerationFunction(),
    DateTimeGenerationFunction()
)
