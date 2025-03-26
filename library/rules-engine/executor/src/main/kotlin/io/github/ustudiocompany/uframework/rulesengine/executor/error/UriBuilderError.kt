package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed class UriBuilderError : RuleEngineError() {

    public class InvalidUriTemplate(public val template: String, cause: Throwable) : UriBuilderError() {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of creating URI from the template: '$template'."
        override val cause: Failure.Cause = Failure.Cause.Exception(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_TEMPLATE to template
        )
    }

    public companion object {
        private const val PREFIX = "RULES-ENGINE-URI-TEMPLATE-"
        private const val DETAILS_KEY_TEMPLATE = "uri-template"
    }
}
