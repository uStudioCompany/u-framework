package io.github.ustudiocompany.uframework.messaging.message.header

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure

public sealed class HeaderErrors : Failure {
    public abstract val name: String

    public class Missing(override val name: String) : HeaderErrors() {
        override val code: String = PREFIX + "1"
        override val description: String = "The `$name` header of a message is missing."
        override val details: Details = Details.of(HEADER_NAME_DETAIL_KEY to name)
    }

    public class InvalidValue(override val name: String, cause: Failure) : HeaderErrors() {
        override val code: String = PREFIX + "2"
        override val description: String = "The `$name` header has invalid value."
        override val cause: Cause = Cause.Failure(cause)
        override val details: Details = Details.of(HEADER_NAME_DETAIL_KEY to name)
    }

    private companion object {
        private const val PREFIX = "HEADER-"
        private const val HEADER_NAME_DETAIL_KEY = "header-name"
    }
}
