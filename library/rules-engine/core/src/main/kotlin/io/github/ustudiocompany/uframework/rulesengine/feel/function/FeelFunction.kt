package io.github.ustudiocompany.uframework.rulesengine.feel.function

import org.camunda.feel.context.JavaFunction

internal interface FeelFunction {
    public val name: String
    public val body: JavaFunction
}
