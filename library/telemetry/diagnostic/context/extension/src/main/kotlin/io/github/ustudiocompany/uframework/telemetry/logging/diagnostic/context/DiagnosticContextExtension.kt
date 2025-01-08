package io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.fullCode

private const val ERROR_CODE_DIAGNOSTIC_CONTEXT_KEY = "error-code"

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    block: DiagnosticContext.() -> T
): T = block(this + failure)

public inline fun <T, E : Failure> DiagnosticContext.withDiagnosticContext(
    failure: E,
    vararg entries: DiagnosticContext.Entry?,
    block: DiagnosticContext.() -> T
): T {
    val diagnosticContext = entries.fold(this + failure) { acc, element -> acc + element }
    return block(diagnosticContext)
}

public operator fun <E : Failure> DiagnosticContext.plus(error: E): DiagnosticContext =
    error.details
        .fold(this) { acc, detail -> acc + DiagnosticContext.Entry(key = detail.key, value = detail.value) }
        .plus(entry(ERROR_CODE_DIAGNOSTIC_CONTEXT_KEY, error.fullCode()))
