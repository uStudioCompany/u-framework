package io.github.ustudiocompany.uframework.saga.engine.error

import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.ReplyHandlerErrors

public sealed class SagaExecutorErrors : SagaErrors {

    /**
     * Екземпляр саги не активний (S-2)
     */
    public data object SagaIsNotActive : SagaExecutorErrors() {
        override val code: String = PREFIX + "1"
        override val description: String = "The saga instance is not active."
    }

    /**
     * Помилка ініціалізації початкових даних екземпляру саги (D-0).
     */
    public class DataInitialize(cause: Failure) : SagaExecutorErrors() {
        override val code: String = PREFIX + "2"
        override val description: String = "An error of initialize the saga instance initial data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    /**
     * Помилка десеріалізації даних екземпляру саги (D-1)
     */
    public class DataDeserialization(cause: Failure) : SagaExecutorErrors() {
        override val code: String = PREFIX + "3"
        override val description: String = "An error of deserialization the saga instance data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    /**
     *  Помилка серіалізації даних екземпляру саги (D-2)
     */
    public class DataSerialization(cause: Failure) : SagaExecutorErrors() {
        override val code: String = PREFIX + "4"
        override val description: String = "An error of serialization the saga instance data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    /**
     * Неочікувана відповідь, не знайдена команда в історії кроків на яку прийшла відповідь (R-2)
     */
    public data object ReplyNotRelevant : SagaExecutorErrors() {
        override val code: String = PREFIX + "5"
        override val description: String = "The reply is not relevant."
    }

    /**
     *  Помилка оновлення даних екземпляру саги (D-3)
     */
    public sealed class Reply : SagaExecutorErrors() {

        override val description: String = "An error of handling a reply."

        public class ReplyBodyMissing(cause: ReplyHandlerErrors.ReplyBodyMissing) : Reply() {
            override val code: String = PREFIX + "REPLY-1"
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        public class ReplyBodyDeserialization(cause: ReplyHandlerErrors.ReplyBodyDeserialization) : Reply() {
            override val code: String = PREFIX + "REPLY-2"
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }

        public class ReplyHandle(cause: ReplyHandlerErrors.ReplyHandle) : Reply() {
            override val code: String = PREFIX + "REPLY-3"
            override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        }
    }

    /**
     * Сага не містить крок на який посилається крок з історії (R-3)
     */
    public class UnknownStep(index: Int, label: SagaStepLabel) : SagaExecutorErrors() {
        override val code: String = PREFIX + "6"
        override val description: String = "The saga does not contain a step that is referenced by a step in history."

        override val details: Details = Details.of(
            STEP_INDEX_DETAIL_KEY to index.toString(),
            STEP_LABEL_DETAIL_KEY to label.get
        )
    }

    public data object CompensationCommandError : SagaExecutorErrors() {
        override val code: String = PREFIX + "7"
        override val description: String = "An error of compensation command."
    }

    /**
     * Помилка створення запиту (MRFC-1)
     */
    public class MakeRequest(cause: Failure) : SagaExecutorErrors() {
        override val code: String = PREFIX + "8"
        override val description: String = "An error of create a request."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public companion object {
        private const val PREFIX = "SAGA-EXECUTOR-"
        public const val STEP_INDEX_DETAIL_KEY: String = "step-index"
        public const val STEP_LABEL_DETAIL_KEY: String = "step-label"
    }
}
