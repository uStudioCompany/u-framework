package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStepExecuteError
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStepExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStepExecuteError

public sealed interface RulesEngineExecutorError : BasicRulesEngineError {

    public class CheckingConditionSatisfactionRule internal constructor(
        cause: CheckingConditionSatisfactionErrors
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of a rule."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataRetrievingStepExecute internal constructor(
        cause: DataRetrieveStepExecuteErrors
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "2"
        override val description: String = "The error of execution the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataBuildStepExecute internal constructor(
        cause: DataBuildStepExecuteError
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "3"
        override val description: String = "The error of execution the 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class ValidationStepExecute internal constructor(
        cause: ValidationStepExecuteError
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "4"
        override val description: String = "The error of execution the 'Validation' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class MessagePublishStepExecute internal constructor(
        cause: MessagePublishStepExecuteErrors
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "5"
        override val description: String = "The error of execution the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class DataChangeTrackingStepExecute internal constructor(
        cause: DataChangeTrackingStepExecuteErrors
    ) : RulesEngineExecutorError {
        override val code: String = PREFIX + "5"
        override val description: String = "The error of execution the 'Data Change Tracking' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULES-EXECUTION-"
    }
}
