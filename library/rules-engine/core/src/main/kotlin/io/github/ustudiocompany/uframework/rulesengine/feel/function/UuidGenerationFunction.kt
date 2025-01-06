package io.github.ustudiocompany.uframework.rulesengine.feel.function

import org.camunda.feel.context.JavaFunction
import org.camunda.feel.syntaxtree.ValString
import java.util.*

internal class UuidGenerationFunction : FeelFunction {
    override val name: String = "uuid"
    override val body: JavaFunction = JavaFunction(emptyList()) { _ ->
        ValString(generate())
    }

    companion object {

        fun generate(): String = UUID.randomUUID().toString()
    }
}
