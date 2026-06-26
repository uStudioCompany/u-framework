package io.github.ustudiocompany.uframework.rulesengine.executor.incident

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepId

public sealed class StepExecutionIncident(stepId: StepId) : BasicRulesEngineIncident {

    override val details: Failure.Details = Failure.Details.of(STEP_ID to stepId.get)

    public class DataRetrieve internal constructor(
        stepId: StepId,
        cause: DataRetrieveStepExecutionIncident
    ) : StepExecutionIncident(stepId) {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident executing the 'Data Retrieve' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataBuild internal constructor(
        stepId: StepId,
        cause: DataBuildStepExecutionIncident
    ) : StepExecutionIncident(stepId) {
        override val code: String = PREFIX + "2"
        override val description: String = "Incident executing the 'Data Build' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class MessagePublish internal constructor(
        stepId: StepId,
        cause: MessagePublishStepExecutionIncident
    ) : StepExecutionIncident(stepId) {
        override val code: String = PREFIX + "3"
        override val description: String = "Incident executing the 'Message Publish' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataChangeTracking internal constructor(
        stepId: StepId,
        cause: DataChangeTrackingStepExecutionIncident
    ) : StepExecutionIncident(stepId) {
        override val code: String = PREFIX + "4"
        override val description: String = "Incident executing the 'Data Change Tracking' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class HttpCall internal constructor(
        stepId: StepId,
        cause: HttpCallStepExecutionIncident
    ) : StepExecutionIncident(stepId) {
        override val code: String = PREFIX + "5"
        override val description: String = "Incident executing the 'HTTP Call' step with id '${stepId.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "STEP-EXECUTION-INCIDENT-"
        private const val STEP_ID = "step-id"
    }
}
