package io.github.ustudiocompany.uframework.eventsourcing

import io.github.ustudiocompany.uframework.failure.Failure

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
            override val details: Failure.Details = Failure.Details.NONE
        }
    }

    public sealed class Snapshot : EventSourceRepositoryErrors() {
        override val domain: String
            get() = super.domain + "-SNAPSHOT"

        public class Load(failure: Failure) : Snapshot() {
            override val number: String = "2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading snapshot."
            override val details: Failure.Details = Failure.Details.NONE
        }

        public class Save(failure: Failure) : Snapshot() {
            override val number: String = "3"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving snapshot."
            override val details: Failure.Details = Failure.Details.NONE
        }
    }

    public sealed class Event : EventSourceRepositoryErrors() {
        override val domain: String
            get() = super.domain + "-EVENT"

        public class Load(failure: Failure) : Event() {
            override val number: String = "2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading event."
            override val details: Failure.Details = Failure.Details.NONE
        }

        public class Save(failure: Failure) : Event() {
            override val number: String = "3"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving event."
            override val details: Failure.Details = Failure.Details.NONE
        }
    }
}
