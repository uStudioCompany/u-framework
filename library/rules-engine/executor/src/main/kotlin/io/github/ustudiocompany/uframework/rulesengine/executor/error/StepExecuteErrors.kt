package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStepExecuteError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStepExecuteError

public sealed class StepExecuteErrors(stepId: StepId) : BasicRulesEngineError {

    override val details: Failure.Details = Failure.Details.of(STEP_ID to stepId.get)

    public class CheckingConditionSatisfaction internal constructor(
        stepId: StepId,
        cause: CheckingConditionSatisfactionErrors
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error checking condition satisfaction of the step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataRetrieving internal constructor(
        stepId: StepId,
        cause: DataRetrieveStepExecuteErrors
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "2"
        override val description: String =
            "The error of execution the 'Data Retrieve' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataBuilding internal constructor(
        stepId: StepId,
        cause: DataBuildStepExecuteError
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "3"
        override val description: String =
            "The error of execution the 'Data Build' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class Validation internal constructor(
        stepId: StepId,
        cause: ValidationStepExecuteError
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "4"
        override val description: String =
            "The error of execution the 'Validation' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class MessagePublishing internal constructor(
        stepId: StepId,
        cause: MessagePublishStepExecuteErrors
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "5"
        override val description: String =
            "The error of execution the 'Message Publish' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataChangeTracking internal constructor(
        stepId: StepId,
        cause: DataChangeTrackingStepExecuteErrors
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "6"
        override val description: String =
            "The error of execution the 'Data Change Tracking' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class HttpCalling internal constructor(
        stepId: StepId,
        cause: HttpCallStepExecuteErrors
    ) : StepExecuteErrors(stepId) {
        override val code: String = PREFIX + "7"
        override val description: String =
            "The error of execution the 'HTTP Call' step '$stepId'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "STEP-EXECUTION-"
        private const val STEP_ID = "step-id"
    }
}
