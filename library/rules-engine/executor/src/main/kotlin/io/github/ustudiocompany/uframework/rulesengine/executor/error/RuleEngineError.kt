package io.github.ustudiocompany.uframework.rulesengine.executor.error

public sealed interface RuleEngineError {
    public val message: String
    public val cause: Throwable?
}
