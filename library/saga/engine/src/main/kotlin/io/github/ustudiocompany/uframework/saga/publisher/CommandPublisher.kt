package io.github.ustudiocompany.uframework.saga.publisher

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext

public fun interface CommandPublisher {

    context(Logging, DiagnosticContext)
    public fun publish(command: Command): Result<Unit, Failure>
}
