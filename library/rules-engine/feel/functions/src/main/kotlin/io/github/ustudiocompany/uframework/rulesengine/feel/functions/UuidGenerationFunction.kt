package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.ustudiocompany.uframework.rulesengine.feel.FeelFunction
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.ValString
import java.util.*

/**
 * The function generates a random UUID.
 * @param none The function does not take any parameters.
 * @return The function returns a string represented in UUID format.
 */
public class UuidGenerationFunction : FeelFunction {
    override val name: String = "uuid"
    override val body: JavaFunction = JavaFunction(emptyList()) { _ ->
        ValString(generate())
    }

    public companion object {
        private fun generate(): String = UUID.randomUUID().toString()
    }
}
