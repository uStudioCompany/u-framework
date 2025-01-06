package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.ustudiocompany.uframework.rulesengine.feel.function.DateTimeGenerationFunction
import io.github.ustudiocompany.uframework.rulesengine.feel.function.FeelFunction
import io.github.ustudiocompany.uframework.rulesengine.feel.function.UuidGenerationFunction

internal fun defaultFunctionRegistry(): Iterable<FeelFunction> = listOf(
    UuidGenerationFunction(),
    DateTimeGenerationFunction()
)
