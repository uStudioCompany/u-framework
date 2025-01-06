package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.ustudiocompany.uframework.rulesengine.feel.function.FeelFunction
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.context.JavaFunctionProvider
import java.util.*

internal class FeelFunctionProvider(list: Iterable<FeelFunction>) : JavaFunctionProvider() {
    private val registry: Map<String, JavaFunction> = registry(list)

    override fun resolveFunction(functionName: String): Optional<JavaFunction> =
        Optional.ofNullable(registry[functionName])

    override fun getFunctionNames(): Collection<String> = registry.keys

    private fun registry(list: Iterable<FeelFunction>): Map<String, JavaFunction> =
        list.associateBy(keySelector = FeelFunction::name, valueTransform = FeelFunction::body)
}
