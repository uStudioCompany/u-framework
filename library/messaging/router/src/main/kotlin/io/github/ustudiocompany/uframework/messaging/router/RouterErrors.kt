package io.github.ustudiocompany.uframework.messaging.router

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_NAME_HEADER_NAME
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_VERSION_HEADER_NAME

public sealed class RouterErrors : Failure {

    public class RouteNotFound(selector: RouteSelector) : RouterErrors() {
        override val code: String = PREFIX + "1"
        override val description: String =
            "A router by selector ($selector) is missing."
        override val details: Details = Details.of(
            MESSAGE_NAME_HEADER_NAME to selector.name.toString(),
            MESSAGE_VERSION_HEADER_NAME to selector.version.toString()
        )
    }

    public sealed class MessageNameHeader : RouterErrors() {

        override val details: Details =
            Details.of(HEADER_NAME_DETAIL_KEY to MESSAGE_NAME_HEADER_NAME)

        public data object Missing : MessageNameHeader() {
            override val code: String = PREFIX + "HEADER-MESSAGE-NAME-1"
            override val description: String =
                "The `$MESSAGE_NAME_HEADER_NAME` header is missing."
        }

        public class InvalidValue(failure: Failure) : MessageVersionHeader() {
            override val code: String = PREFIX + "HEADER-MESSAGE-NAME-1"
            override val description: String =
                "The `$MESSAGE_NAME_HEADER_NAME` header is invalid data."
            override val cause: Cause = Cause.Failure(failure)
        }
    }

    public sealed class MessageVersionHeader : MessageNameHeader() {

        override val details: Details =
            Details.of(HEADER_NAME_DETAIL_KEY to MESSAGE_VERSION_HEADER_NAME)

        public data object Missing : MessageVersionHeader() {
            override val code: String = PREFIX + "HEADER-MESSAGE-VERSION-1"
            override val description: String =
                "The `$MESSAGE_VERSION_HEADER_NAME` header is missing."
        }

        public class InvalidValue(failure: Failure) : MessageVersionHeader() {
            override val code: String = PREFIX + "HEADER-MESSAGE-VERSION-2"
            override val description: String =
                "The `$MESSAGE_VERSION_HEADER_NAME` header is invalid data."
            override val cause: Cause = Cause.Failure(failure)
        }
    }

    private companion object {
        private const val PREFIX = "ROUTER-"
        private const val HEADER_NAME_DETAIL_KEY = "message-header"
    }
}
