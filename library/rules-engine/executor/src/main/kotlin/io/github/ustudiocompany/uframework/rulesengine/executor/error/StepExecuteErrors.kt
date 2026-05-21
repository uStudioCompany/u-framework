package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStepExecuteError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStepExecuteError

public sealed interface StepExecuteErrors : BasicRulesEngineError {

    public class DataRetrieving internal constructor(
        stepId: StepId,
        cause: DataRetrieveStepExecuteErrors
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "The error of execution the 'Data Retrieve' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    public class DataBuilding internal constructor(
        stepId: StepId,
        cause: DataBuildStepExecuteError
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "The error of execution the 'Data Build' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    public class Validation internal constructor(
        stepId: StepId,
        cause: ValidationStepExecuteError
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String =
            "The error of execution the 'Validation' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    public class MessagePublishing internal constructor(
        stepId: StepId,
        cause: MessagePublishStepExecuteErrors
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "4"
        override val description: String =
            "The error of execution the 'Message Publish' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    public class DataChangeTracking internal constructor(
        stepId: StepId,
        cause: DataChangeTrackingStepExecuteErrors
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "5"
        override val description: String =
            "The error of execution the 'Data Change Tracking' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    public class HttpCalling internal constructor(
        stepId: StepId,
        cause: HttpCallStepExecuteErrors
    ) : StepExecuteErrors {
        override val code: String = PREFIX + "6"
        override val description: String =
            "The error of execution the 'HTTP Call' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            STEP_ID to stepId.get
        )
    }

    private companion object {
        private const val PREFIX = "STEP-EXECUTION-"
        private const val STEP_ID = "step-id"
    }
}
