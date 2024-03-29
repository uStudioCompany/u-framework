package com.dream.umbrella.lib.eventsourcing

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors

public sealed class EventSourceRepositoryErrors : Failure {
    override val domain: String
        get() = "REPOSITORY"

    public sealed class Aggregate : EventSourceRepositoryErrors() {
        override val domain: String
            get() = super.domain + "-AGGREGATE"

        public class Create(failure: Failure) : Aggregate() {
            override val number: String = "1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading aggregate."
            override val details: Failure.Details = Failure.Details.None
        }
    }

    public sealed class Snapshot : EventSourceRepositoryErrors() {
        override val domain: String
            get() = super.domain + "-SNAPSHOT"

        public class Connection(failure: JDBCErrors.Connection) : Snapshot() {
            override val number: String = "1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The connection error."
            override val details: Failure.Details = Failure.Details.None
        }

        public class Load(failure: JDBCErrors) : Snapshot() {
            override val number: String = "2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading snapshot."
            override val details: Failure.Details = Failure.Details.None
        }

        public class Save(failure: JDBCErrors) : Snapshot() {
            override val number: String = "3"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving snapshot."
            override val details: Failure.Details = Failure.Details.None
        }

        public class InvalidData(failure: Failure) : Snapshot() {
            override val number: String = "4"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "Invalid data"
            override val details: Failure.Details = Failure.Details.None
        }

        public class Deserialization(cause: Failure) : Snapshot() {
            override val number: String = "5"
            override val description: String = "The error of deserialization the snapshot data."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        public class Serialization(cause: Failure) : Snapshot() {
            override val number: String = "6"
            override val description: String = "The error of serialization the snapshot data."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        public sealed class History : Snapshot() {
            override val domain: String
                get() = super.domain + "-HISTORY"

            public class Deserialization(cause: Failure) : History() {
                override val number: String = "1"
                override val description: String = "The error of deserialization the snapshot history."
                override val cause: Failure.Cause = Failure.Cause.Failure(cause)
            }

            public class Serialization(cause: Failure) : History() {
                override val number: String = "2"
                override val description: String = "The error of serialization the snapshot history."
                override val cause: Failure.Cause = Failure.Cause.Failure(cause)
            }
        }
    }

    public sealed class Event : EventSourceRepositoryErrors() {
        override val domain: String
            get() = super.domain + "-EVENT"

        public class Connection(failure: JDBCErrors.Connection) : Event() {
            override val number: String = "1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The connection error."
            override val details: Failure.Details = Failure.Details.None
        }

        public class Load(failure: JDBCErrors) : Event() {
            override val number: String = "2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading event."
            override val details: Failure.Details = Failure.Details.None
        }

        public class Save(failure: JDBCErrors) : Event() {
            override val number: String = "3"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving event."
            override val details: Failure.Details = Failure.Details.None
        }

        public data object Missing : Event() {
            override val number: String = "4"
            override val description: String = "The event is missing."
            override val details: Failure.Details = Failure.Details.None
        }

        public class InvalidData(failure: Failure) : Event() {
            override val number: String = "5"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "Invalid data."
            override val details: Failure.Details = Failure.Details.None
        }

        public class UnknownName(name: String) : Event() {
            override val number: String = "6"
            override val description: String = "Unknown event name `$name`."
            override val details: Failure.Details = Failure.Details.of(EVENT_NAME_DETAILS_KEY to name)
        }

        public class Deserialization(cause: Failure) : Event() {
            override val number: String = "7"
            override val description: String = "The error of deserialization the event data."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        public class Serialization(cause: Failure) : Event() {
            override val number: String = "8"
            override val description: String = "The error of serialization the event data."
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        private companion object {
            private const val EVENT_NAME_DETAILS_KEY = "event-name"
        }
    }
}
