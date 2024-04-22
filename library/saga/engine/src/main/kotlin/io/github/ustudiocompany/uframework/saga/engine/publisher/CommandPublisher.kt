package io.github.ustudiocompany.uframework.saga.engine.publisher

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public fun interface CommandPublisher {

    context(Logging, DiagnosticContext)
    public fun publish(command: Command): Result<Unit, Failure>
}
