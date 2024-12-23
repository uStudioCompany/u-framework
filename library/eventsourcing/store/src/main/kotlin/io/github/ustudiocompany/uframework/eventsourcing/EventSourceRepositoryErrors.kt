package io.github.ustudiocompany.uframework.eventsourcing

import io.github.ustudiocompany.uframework.failure.Failure

public sealed class EventSourceRepositoryErrors : Failure {

    public sealed class Aggregate : EventSourceRepositoryErrors() {

        public class Create(failure: Failure) : Aggregate() {
            override val code: String = PREFIX + "AGGREGATE-1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading aggregate."
            override val details: Failure.Details = Failure.Details.NONE
        }
    }

    public sealed class Snapshot : EventSourceRepositoryErrors() {

        public class Load(failure: Failure) : Snapshot() {
            override val code: String = PREFIX + "SNAPSHOT-1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading snapshot."
            override val details: Failure.Details = Failure.Details.NONE
        }

        public class Save(failure: Failure) : Snapshot() {
            override val code: String = PREFIX + "SNAPSHOT-2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving snapshot."
            override val details: Failure.Details = Failure.Details.NONE
        }
    }

    public sealed class Event : EventSourceRepositoryErrors() {

        public class Load(failure: Failure) : Event() {
            override val code: String = PREFIX + "EVENT-1"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of loading event."
            override val details: Failure.Details = Failure.Details.NONE
        }

        public class Save(failure: Failure) : Event() {
            override val code: String = PREFIX + "EVENT-2"
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
            override val description: String = "The error of saving event."
            override val details: Failure.Details = Failure.Details.NONE
        }
    }

    private companion object {
        private const val PREFIX = "REPOSITORY-"
    }
}
