package io.github.ustudiocompany.uframework.messaging.handler

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.exceptionOrNull
import io.github.ustudiocompany.uframework.failure.fullDescription
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext

context(DiagnosticContext)
public fun Failure.toMessageHandlerException(description: String? = null): MessageHandlerException =
    MessageHandlerException.make(this, description)

context(DiagnosticContext)
public fun Exception.toMessageHandlerException(description: String): MessageHandlerException =
    MessageHandlerException.make(this, description)

public class MessageHandlerException private constructor(
    public val description: String,
    public val diagnosticContext: DiagnosticContext,
    cause: Throwable?
) : RuntimeException(description, cause) {

    public companion object {

        context(DiagnosticContext)
        public fun make(failure: Failure, description: String? = null): MessageHandlerException {
            val descriptionPrefix = if (description != null) "$description " else ""
            withDiagnosticContext(failure) {
                return MessageHandlerException(
                    description = descriptionPrefix + failure.fullDescription(),
                    diagnosticContext = this,
                    cause = failure.exceptionOrNull()
                )
            }
        }

        context(DiagnosticContext)
        public fun make(exception: Throwable, description: String): MessageHandlerException =
            MessageHandlerException(
                description = description,
                diagnosticContext = this@DiagnosticContext,
                cause = exception
            )
    }
}
