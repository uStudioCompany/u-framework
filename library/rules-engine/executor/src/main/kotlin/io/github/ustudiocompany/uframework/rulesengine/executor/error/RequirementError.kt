package io.github.ustudiocompany.uframework.rulesengine.executor.error

public class RequirementError(public val code: String) : RuleEngineError {
    override val message: String = "Requirement validation error: `$code`"
    override val cause: Throwable? = null
}
