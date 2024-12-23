package io.github.ustudiocompany.uframework.rulesengine.executor.error

public class RequirementError(override val code: String) : RuleEngineError {
    override val description: String = "Requirement validation error: `$code`."
}
