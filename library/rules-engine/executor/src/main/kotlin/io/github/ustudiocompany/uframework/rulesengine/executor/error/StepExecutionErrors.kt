package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.ConditionEvaluationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStepExecutingError

public sealed class StepExecutionErrors(stepId: StepId) : BasicRulesEngineError {

    override val details: Failure.Details = Failure.Details.of(STEP_ID to stepId.get)

    public class ConditionEvaluation internal constructor(
        stepId: StepId,
        cause: ConditionEvaluationErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error condition evaluation in the step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataRetrieve internal constructor(
        stepId: StepId,
        cause: DataRetrieveStepExecutionErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "2"
        override val description: String = "Error executing the 'Data Retrieve' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataBuild internal constructor(
        stepId: StepId,
        cause: DataBuildStepExecutionErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "3"
        override val description: String = "Error executing the 'Data Build' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class Validation internal constructor(
        stepId: StepId,
        cause: ValidationStepExecutingError
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "4"
        override val description: String = "Error executing the 'Validation' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class MessagePublish internal constructor(
        stepId: StepId,
        cause: MessagePublishStepExecutionErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "5"
        override val description: String = "Error executing the 'Message Publish' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataChangeTracking internal constructor(
        stepId: StepId,
        cause: DataChangeTrackingStepExecutionErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "6"
        override val description: String = "Error executing the 'Data Change Tracking' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class HttpCall internal constructor(
        stepId: StepId,
        cause: HttpCallStepExecutionErrors
    ) : StepExecutionErrors(stepId) {
        override val code: String = PREFIX + "7"
        override val description: String = "Error executing the 'HTTP Call' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "STEP-EXECUTION-ERROR-"
        private const val STEP_ID = "step-id"
    }
}
