package io.github.ustudiocompany.uframework.rulesengine.feel.functions

import io.github.ustudiocompany.uframework.rulesengine.feel.FeelFunction
import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.ValString
import java.util.UUID

/**
 * The function generates a UUID.
 * @param args The function takes any number of parameters, which are used to generate the UUID.
 * @return The function returns a string represented in UUID format.
 */
public class UuidGenerationFunction : FeelFunction {
    override val name: String = "uuid"
    override val body: JavaFunction = JavaFunction(
        emptyList(),
        { args ->
            if (args.isNotEmpty()) {
                val base = args.joinToString(":") { it.toString() }
                ValString(generate(base))
            } else
                ValString(generate())
        },
        true
    )

    public companion object {
        private fun generate(): String = UUID.randomUUID().toString()
        private fun generate(base: String): String = UUID.nameUUIDFromBytes(base.toByteArray()).toString()
    }
}
