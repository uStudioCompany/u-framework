package io.github.ustudiocompany.uframework.rulesengine.feel

import org.camunda.feel.context.JavaFunction

public interface FeelFunction {
    public val name: String
    public val body: JavaFunction
}
