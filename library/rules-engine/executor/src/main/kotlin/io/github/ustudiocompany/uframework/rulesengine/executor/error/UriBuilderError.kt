package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface UriBuilderError : RuleEngineError {

    public class ParamMissing(public val name: String) : UriBuilderError {
        override val code: String = "RULES-ENGINE-CALL-STEP-URI-TEMPLATE-PARAM-MISSING"
        override val description: String = "Param '$name' is missing."
    }

    public class InvalidUriTemplate(public val template: String, cause: Throwable) : UriBuilderError {
        override val code: String = "RULES-ENGINE-CALL-STEP-URI-TEMPLATE-INVALID"
        override val description: String = "The error of creating URI from the template: '$template'."
        override val cause: Failure.Cause = Failure.Cause.Exception(cause)
    }
}
