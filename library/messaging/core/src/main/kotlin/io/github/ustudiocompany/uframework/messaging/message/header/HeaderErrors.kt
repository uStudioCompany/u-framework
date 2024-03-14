package io.github.ustudiocompany.uframework.messaging.message.header

import io.github.ustudiocompany.uframework.failure.Failure

public sealed class HeaderErrors : Failure {
    override val domain: String
        get() = "HEADER"

    public abstract val name: String

    public class Missing(override val name: String) : HeaderErrors() {
        override val number: String = "1"
        override val description: String = "The `$name` header of a message is missing."
        override val details: Failure.Details = Failure.Details.of(HEADER_NAME_DETAIL_KEY to name)
    }

    public class InvalidValue(override val name: String, cause: Failure) : HeaderErrors() {
        override val number: String = "2"
        override val description: String = "The `$name` header has invalid value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(HEADER_NAME_DETAIL_KEY to name)
    }

    private companion object {
        private const val HEADER_NAME_DETAIL_KEY = "header-name"
    }
}
