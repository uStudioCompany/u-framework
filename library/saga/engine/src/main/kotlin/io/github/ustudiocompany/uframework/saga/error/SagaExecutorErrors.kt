package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.step.SagaStepLabel

public sealed class SagaExecutorErrors : SagaErrors {
    override val domain: String
        get() = "SAGA.EXECUTOR"

    /**
     * Екземпляр саги не активний (S-2)
     */
    public data object SagaIsNotActive : SagaExecutorErrors() {
        override val number: String = "1"
        override val description: String = "The saga instance is not active."
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    /**
     * Помилка ініціалізації початкових даних екземпляру саги (D-0).
     */
    public class DataInitialize(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "2"
        override val description: String = "An error of initialize the saga instance initial data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }

    /**
     * Помилка десеріалізації даних екземпляру саги (D-1)
     */
    public class DataDeserialization(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "3"
        override val description: String = "An error of deserialization the saga instance data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }

    /**
     *  Помилка серіалізації даних екземпляру саги (D-2)
     */
    public class DataSerialization(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "4"
        override val description: String = "An error of serialization the saga instance data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }

    /**
     *  Помилка оновлення даних екземпляру саги (D-3)
     */
    public class DataUpdate(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "5"
        override val description: String = "An error of update the saga instance data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    /**
     * Неочікувана відповідь, не знайдена команда в історії кроків на яку прийшла відповідь (R-2)
     */
    public data object ReplyNotRelevant : SagaExecutorErrors() {
        override val number: String = "6"
        override val description: String = "The reply is not relevant."
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    /**
     * Сага не містить крок на який посилається крок з історії (R-3)
     */
    public class UnknownStep(index: Int, label: SagaStepLabel) : SagaExecutorErrors() {
        override val number: String = "7"
        override val description: String = "The saga does not contain a step that is referenced by a step in history."

        override val details: Failure.Details = Failure.Details.of(
            SagaErrors.STEP_INDEX_DETAIL_KEY to index.toString(),
            SagaErrors.STEP_LABEL_DETAIL_KEY to label.get
        )

        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }

    /**
     * Відповідь не містить тіло (R-4)
     */
    public class ReplyBodyMissing : SagaExecutorErrors() {
        override val number: String = "8"
        override val description: String = "The reply body is missing."
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    /**
     * Помилка десеріалізації тіла відповіді (R-5)
     */
    public class ReplyBodyDeserialization(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "9"
        override val description: String = "An error of deserialize the reply body."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    /**
     * R-6
     */
    public class ReplyHandle(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "10"
        override val description: String = "An error of handle the reply."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    public data object CompensationCommandError : SagaExecutorErrors() {
        override val number: String = "11"
        override val description: String = "An error of compensation command."
        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }

    /**
     * Помилка створення запиту (MRFC-1)
     */
    public class MakeRequest(cause: Failure) : SagaExecutorErrors() {
        override val number: String = "12"
        override val description: String = "An error of create a request."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }
}
