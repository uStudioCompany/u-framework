package io.github.ustudiocompany.uframework.messaging.router

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_NAME_HEADER_NAME
import io.github.ustudiocompany.uframework.messaging.header.MESSAGE_VERSION_HEADER_NAME

public sealed class RoutingErrors : Failure {

    override val domain: String = "ROUTING"

    override val kind: Failure.Kind
        get() = Failure.Kind.ERROR

    public class RouteNotFound(selector: RouteSelector) : RoutingErrors() {
        override val number: String = "1"
        override val description: String =
            "A router by selector ($selector) is missing."
        override val details: Failure.Details = Failure.Details.of(
            MESSAGE_NAME_HEADER_NAME to selector.name.toString(),
            MESSAGE_VERSION_HEADER_NAME to selector.version.toString()
        )
    }

    public sealed class MessageNameHeader : RoutingErrors() {

        override val domain: String
            get() = super.domain + ".HEADER.MESSAGE.NAME"

        override val details: Failure.Details =
            Failure.Details.of(HEADER_NAME_DETAIL_KEY to MESSAGE_NAME_HEADER_NAME)

        public data object Missing : MessageNameHeader() {
            override val number: String = "1"
            override val description: String =
                "The `$MESSAGE_NAME_HEADER_NAME` header is missing."
        }

        public class InvalidValue(failure: Failure) : MessageVersionHeader() {
            override val number: String = "2"
            override val description: String =
                "The `$MESSAGE_NAME_HEADER_NAME` header is invalid data."
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
        }
    }

    public sealed class MessageVersionHeader : MessageNameHeader() {
        override val domain: String
            get() = super.domain + ".HEADER.MESSAGE.VERSION"

        override val details: Failure.Details =
            Failure.Details.of(HEADER_NAME_DETAIL_KEY to MESSAGE_VERSION_HEADER_NAME)

        public data object Missing : MessageVersionHeader() {
            override val number: String = "1"
            override val description: String =
                "The `$MESSAGE_VERSION_HEADER_NAME` header is missing."
        }

        public class InvalidValue(failure: Failure) : MessageVersionHeader() {
            override val number: String = "2"
            override val description: String =
                "The `$MESSAGE_VERSION_HEADER_NAME` header is invalid data."
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
        }
    }

    private companion object {
        private const val HEADER_NAME_DETAIL_KEY = "message-header"
    }
}
